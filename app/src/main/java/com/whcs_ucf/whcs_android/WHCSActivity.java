package com.whcs_ucf.whcs_android;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Jimmy on 6/4/2015.
 */
public class WHCSActivity extends AppCompatActivity implements PipelineErrorHandler {
    public static final String TAG_MAC_STRING = "macString";
    // Well known SPP UUID
    //This SPP is a property of the bluetooth module.
    public static final UUID WHCS_BLUETOOTH_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    protected SharedPreferences mPrefs;
    protected BluetoothAdapter whcsBlueToothAdapter;
    protected static final int REQUEST_ENABLE_BT = 1;
    protected CommandIssuer whcsIssuer;
    protected WHCSBluetoothListener whcsBluetoothListener;
    protected BluetoothDevice baseStationDevice;
    protected IssuerAndListenerInitializerThread initializerThread;

    //Holds state of whether or not the issuer and BlueToothListener duo have been initialized by
    //Giving the listener a BlueToothSocket
    protected static boolean issuerAndListenerInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = getApplicationContext().getSharedPreferences("default",MODE_WORLD_READABLE);
        refreshBlueToothAdapter();

        if(!DebugFlags.RUNNING_ON_VM) {
            enableBluetooth();
        }

        String macString = getIntent().getStringExtra(TAG_MAC_STRING);
        if(macString != null) {
            initializeBaseStation(macString);
        }
    }

    protected void refreshBlueToothAdapter(){
        this.whcsBlueToothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    //Uses the activities BlueToothAdapter to see if BlueTooth is enabled. If it isn't it starts
    //a BlueTooth activation activity.
    private void enableBluetooth()
    {
        if (!whcsBlueToothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);

            Toast.makeText(this.getApplicationContext(), "Bluetooth turned on",
                    Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this.getApplicationContext(),"Bluetooth is already on",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void initializeBaseStation(String macString) {
        baseStationDevice = whcsBlueToothAdapter.getRemoteDevice(macString);
    }

    protected void initIssuerAndListener(BluetoothDevice device) throws IOException {
        if(this.issuerAndListenerInitialized) {
            throw new Error("Tried to initialize an already initialized issuer and listener.");
        }
        whcsIssuer = CommandIssuer.GetSingletonCommandIssuer();
        whcsBluetoothListener = WHCSBluetoothListener.GetSingletonBluetoothListener(whcsIssuer);

        whcsIssuer.setCommandSender(whcsBluetoothListener);
        issuerAndListenerInitialized = true;
        Log.d("WHCS-UCF", "Initialized issuer and listener.");
    }


    /*
    asynchInitIssuerAndListener exists to prevent the GUI thread from being blocked whenever the application
    is trying to connect to the base station. The caller of this function will be starting a thread that sets up
    the WHCSCommandIssuer and the WHCSBluetoothListener. The CommandIssuer is created before the new initializer
    thread is started because creating the issuer is not blocking at all. The main reason the thread is created
    is to solve the problem that comes with the BluetoothListeners need to connect to a bluetooth socket.
    The thread begins and gets the WHCSBluetoothListener, it then tries to start it. A callback function is passed
    to the WHCSBluetoothListeners start method in order to act upon whether or not the WHCSBluetoothListener was able
    to start & connect on the BluetoothDevice's socket.
     */
    protected void asynchInitIssuerAndListener(BluetoothDevice device, ConnectionMadeCallback cb) {
        if(this.issuerAndListenerInitialized) {
            throw new Error("Tried to initialize an already initialized issuer and listener.");
        }
        whcsIssuer = CommandIssuer.GetSingletonCommandIssuer();
        if(initializerThread != null) {
            if(initializerThread.isAlive()) {
                Log.d("WHCS-UCF", "Attempting to stop the initializer thread.");
                stopInitializerThread();
                return;
            }
        }
        initializerThread = new IssuerAndListenerInitializerThread(device, cb);
        initializerThread.start();
    }

    private class IssuerAndListenerInitializerThread extends Thread {
        private boolean shouldStop;
        private BluetoothDevice device;
        private ConnectionMadeCallback cb;

        public IssuerAndListenerInitializerThread(BluetoothDevice device, ConnectionMadeCallback cb) {
            this.device = device;
            this.cb = cb;
            this.shouldStop = false;
        }

        @Override
        public void run() {

            whcsBluetoothListener = WHCSBluetoothListener.GetSingletonBluetoothListener(whcsIssuer);
            try {
                whcsBluetoothListener.start(device, new ConnectionMadeCallback() {
                    @Override
                    public void onSuccessfulConnection() {
                        whcsIssuer.setCommandSender(whcsBluetoothListener);
                        issuerAndListenerInitialized = true;
                        Log.d("WHCS-UCF", "Initialized issuer and listener.");
                        if(!IssuerAndListenerInitializerThread.this.shouldStop) {
                            cb.onSuccessfulConnection();
                        }
                    }

                    @Override
                    public void onTimeoutConnection() {
                        if(!IssuerAndListenerInitializerThread.this.shouldStop) {
                            cb.onTimeoutConnection();
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                cb.onTimeoutConnection();
                return;
            }
        }

        public void setShouldStopTrue() {
            shouldStop = true;
            if(whcsBluetoothListener != null) {
                whcsBluetoothListener.stop();
            }
        }
    }

    protected void stopInitializerThread() {
        if(initializerThread != null) {
            if(initializerThread.isAlive()) {
                initializerThread.setShouldStopTrue();
                initializerThread = null;
            }
        }
    }

    protected void refreshIssuerAndListener() {
        if(!this.issuerAndListenerInitialized) {
            throw new Error("Must initialize issuer and listener before refreshing them.");
        }
        whcsIssuer = CommandIssuer.GetSingletonCommandIssuer();
        whcsBluetoothListener = WHCSBluetoothListener.GetSingletonBluetoothListener(whcsIssuer);
        whcsIssuer.setPipelineErrorHandler(this);
    }

    protected void destroyIssuerAndListener() {
        if(!issuerAndListenerInitialized) {
            return;
        }
        whcsIssuer = CommandIssuer.GetSingletonCommandIssuer();
        whcsBluetoothListener = WHCSBluetoothListener.GetSingletonBluetoothListener(whcsIssuer);
        whcsIssuer.stop();
        whcsBluetoothListener.stop();
        issuerAndListenerInitialized = false;
    }

    protected void saveBaseStationDeviceForStop() {
        mPrefs.edit().putString(TAG_MAC_STRING, whcsBluetoothListener.getBluetoothDevice().getAddress()).commit();
    }

    protected BluetoothDevice loadBaseStationDeviceForStop() {
        String addressString = mPrefs.getString(this.TAG_MAC_STRING, "");
        if(!addressString.equals("")) {
            return BluetoothAdapter.getDefaultAdapter().getRemoteDevice(addressString);
        }
        return null;
    }

    @Override
    public void onCommunicationPipelineError() {
        destroyIssuerAndListener();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}