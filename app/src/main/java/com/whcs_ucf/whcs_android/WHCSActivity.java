package com.whcs_ucf.whcs_android;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by Jimmy on 6/4/2015.
 */
public class WHCSActivity extends AppCompatActivity {
    protected BluetoothAdapter whcsBlueToothAdapter;
    protected static final int REQUEST_ENABLE_BT = 1;
    protected CommandIssuer whcsIssuer;
    protected WHCSBluetoothListener whcsBluetoothListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshBlueToothAdapter();
        if(!DebugFlags.RUNNING_ON_VM) {
            enableBluetooth();
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

    protected void initIssuerAndListener() {
        if(!DebugFlags.DEBUG_BLUETOOTH_COMM_PIPELINE) {
            throw new Error("Not yet implemented for actual communication.");
        }
        whcsIssuer = new CommandIssuer();
        whcsBluetoothListener = new WHCSBluetoothListener(whcsIssuer);
        whcsIssuer.setCommandSender(whcsBluetoothListener);
        whcsIssuer.run();
    }
}