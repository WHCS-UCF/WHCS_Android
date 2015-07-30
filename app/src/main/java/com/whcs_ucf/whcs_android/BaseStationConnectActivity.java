package com.whcs_ucf.whcs_android;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dd.CircularProgressButton;

import java.io.IOException;
import java.util.Set;


public class BaseStationConnectActivity extends WHCSActivityWithCleanup {
    private ListView pairedList;
    private ListView activeList;
    private CircularProgressButton pairedbutton;
    private CircularProgressButton activeButton;

    private ArrayAdapter<String> pairedArrayAdapter;
    private BluetoothDeviceAdapter activeArrayAdapter;

    //some supporting objects are needed to discover active BlueTooth Devices.
    //We need to start an asynchronous device finder and then register a
    //Broadcast receiver to our application that way when the device finder finishes
    //Our application will catch the fact that it is complete.
    //An Intent filter is used to catch the
    private BroadcastReceiver activeDeviceReceiver;
    private Handler asynchronousActivityStartHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_station_connect);
        Log.d("WHCS-UCF", "V1.4");
        this.asynchronousActivityStartHandler = new Handler(Looper.getMainLooper());
        //establishes refs to buttons, lists
        this.setupGUIVariables();
        //sets event listeners for buttons
        this.setupButtonListeners();
        //sets event listeners for clicking list items
        this.setupListListeners();
        //Check to see if a Base Station has been connected to before.
        //If one has, attempt to connect to it.
        this.tryConnectToExistingBasestation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base_station_connect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupGUIVariables() {
        //Establishes the references to the listviews in the basestation connection activity.
        this.pairedList = (ListView)this.findViewById(R.id.pairedListView);
        this.activeList = (ListView)this.findViewById(R.id.activeListView);

        //Listviews need an adapter to function properly. Here we create and add adapters to the
        //listviews
        pairedArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        pairedList.setAdapter(pairedArrayAdapter);

        activeArrayAdapter = new BluetoothDeviceAdapter(this, android.R.layout.simple_list_item_1);
        activeList.setAdapter(activeArrayAdapter);

        //Establishes the references to the buttons in the basestation connection activity.
        this.pairedbutton = (CircularProgressButton)this.findViewById(R.id.pairedButton);
        this.activeButton = (CircularProgressButton)this.findViewById(R.id.activeButton);

        //We want activeButton to be showing when it is scanning for devices.
        this.activeButton.setIndeterminateProgressMode(true);
    }

    private void setupButtonListeners() {
        pairedbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listPairedDevices();
            }
        });

        activeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (activeDeviceReceiver == null) {
                    // Create a BroadcastReceiver for ACTION_FOUND
                    activeDeviceReceiver = new BroadcastReceiver() {
                        public void onReceive(Context context, Intent intent) {
                            String action = intent.getAction();
                            // When discovery finds a device
                            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                                // Get the BluetoothDevice object from the Intent
                                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                                // Add the name and address to an array adapter to show in a ListView
                                activeArrayAdapter.add(device);
                            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                                //We want the active device button to stop the refeshing effect.
                                BaseStationConnectActivity.this.cancelDiscovery();
                            }
                        }
                    };

                    // Register the BroadcastReceiver
                    //This must be called after activeDeviceReceiver is initialized with the BlueTooth Discovery handler
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(activeDeviceReceiver, filter); // Don't forget to unregister during onDestroy

                    //Register to receive when BlueTooth discovery stops.
                    IntentFilter discoveryStopFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                    registerReceiver(activeDeviceReceiver, discoveryStopFilter);
                }

                listActiveDevices();
            }
        });
    }

    private void setupListListeners() {
        pairedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (DebugFlags.START_DEBUG_ACTIVITY_FROM_LIST_VIEW) {
                    startDebugActivity();
                }
            }
        });

        activeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (DebugFlags.DEBUG_CONTROL_MODULE_LIST_ACTIVITY_NO_BASESTATION_CONNECTION) {
                    BaseStationConnectActivity.this.cancelDiscovery();
                    startControlModuleListActivity();
                    return;
                } else if(DebugFlags.PERFORM_DEBUG_BASE_STATION_QUERY_FROM_BASE_STATION_CONNECT_ACTIVITY) {
                    BaseStationConnectActivity.this.cancelDiscovery();
                    try {
                        if(issuerAndListenerInitialized) {
                            destroyIssuerAndListener();
                            return;
                        }
                        BaseStationConnectActivity.this.initIssuerAndListener(activeArrayAdapter.getItem(position));
                        BaseStationConnectActivity.this.whcsIssuer.queueCommand(WHCSCommand.CreateQueryIfBaseStationCommand(), new ClientCallback() {
                            @Override
                            public void onResponse(WHCSCommand command, WHCSResponse response) {
                                Log.d("WHCS-UCF", "It' the base station.");
                            }
                            @Override
                            public void onTimeOut(WHCSCommand command) {
                                Log.d("WHCS-UCF", "Timeout trying to connect to base station.");
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("WHCS-UCF", "beginning to initialize issuer and listener.");
                    BaseStationConnectActivity.this.cancelDiscovery();
                    if(issuerAndListenerInitialized) {
                        destroyIssuerAndListener();
                        Toast.makeText(BaseStationConnectActivity.this.getApplicationContext(), "Retry in 1 second.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    performAsynchInitialization(activeArrayAdapter.getItem(position));
                }
            }
        });
    }

    private void tryConnectToExistingBasestation() {
        if(loadBaseStationDeviceForStop() != null) {
            performAsynchInitialization(loadBaseStationDeviceForStop());
        }
    }

    private void performAsynchInitialization(BluetoothDevice device) {
        BaseStationConnectActivity.this.asynchInitIssuerAndListener(device, new ConnectionMadeCallback() {
            @Override
            public void onSuccessfulConnection() {
                BaseStationConnectActivity.this.whcsIssuer.queueCommand(WHCSCommand.CreateQueryIfBaseStationCommand(), new ClientCallback() {
                    @Override
                    public void onResponse(WHCSCommand command, WHCSResponse response) {
                        Log.d("WHCS-UCF", "It' the base station.");
                        saveBaseStationDeviceForStop();
                        asynchronousActivityStartHandler.post(new ControlModuleListActivityStarter());
                    }

                    @Override
                    public void onTimeOut(WHCSCommand command) {
                        Log.d("WHCS-UCF", "Timeout trying to connect to base station.");
                        if(whcsIssuer != null) {
                            whcsIssuer.stop();
                        }
                        if(whcsBluetoothListener != null) {
                            whcsBluetoothListener.stop();
                        }
                        //Toast.makeText(BaseStationConnectActivity.this.getApplicationContext(), "Could not initialize BluetoothConnection", Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void onTimeoutConnection() {
                Log.d("WHCS-UCF", "Asynch initialization of issuer and listener timed out.");
            }
        });
    }

    private void listPairedDevices() {

        if(DebugFlags.RUNNING_ON_VM){
            listDevicesForVM(pairedArrayAdapter);
            return;
        }

        // get paired devices

        Set<BluetoothDevice> pairedDevices = whcsBlueToothAdapter.getBondedDevices();

        //Clear everything currently in the list's adapter
        pairedArrayAdapter.clear();

        // put it's one to the adapter
        for(BluetoothDevice device : pairedDevices)
            pairedArrayAdapter.add(device.getName()+ "\n" + device.getAddress());
    }

    private void listActiveDevices() {

        if(DebugFlags.RUNNING_ON_VM){
            listDevicesForVM(activeList);
            return;
        }
        if(whcsBlueToothAdapter.isDiscovering()) {
            //We're already discovering. We need to wait for that to finish.
            return;
        }
        //Clear the arrayAdapter for a refresh
        activeArrayAdapter.clear();


        //Begin discovery but notify through text if we weren't able to for some reason.
        if(!whcsBlueToothAdapter.startDiscovery()) {
            Toast.makeText(this.getApplicationContext(), "BlueTooth Discovery couldn't start.", Toast.LENGTH_LONG).show();
            return;
        }
        //Set progress between 1-99 to get the refreshing look.
        this.activeButton.setProgress(50);

    }

    private class ControlModuleListActivityStarter implements Runnable {

        @Override
        public void run() {
            startControlModuleListActivity();
        }
    }

    private void listDevicesForVM(ArrayAdapter<String> aa) {
        aa.clear();
        for(int i=0; i<5; i++) {
            aa.add("Fake Device number #" + i);
        }
    }

    private void listDevicesForVM(ListView lv) {
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        aa.clear();
        for(int i=0; i<5; i++) {
            aa.add("Fake Device number #" + i);
        }
        lv.setAdapter(aa);
    }

    private void startDebugActivity() {
        Intent intent = new Intent(getApplicationContext(), DebugActivity.class);
        startActivity(intent);
    }

    private void startControlModuleListActivity() {
        Intent intent = new Intent(getApplicationContext(), ControlModuleListActivity.class);
        startActivity(intent);
    }

    private void cancelDiscovery() {
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        if(activeDeviceReceiver != null) {
            this.unregisterReceiver(activeDeviceReceiver);
            activeDeviceReceiver = null;
        }
        this.activeButton.setProgress(0);
    }



    @Override
    protected void onStop() {
        cancelDiscovery();
        stopInitializerThread();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
