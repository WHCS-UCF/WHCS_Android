package com.whcs_ucf.whcs_android;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Jimmy on 6/4/2015.
 */
public class WHCSActivity extends AppCompatActivity {
    private BluetoothAdapter whcsBlueToothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshBlueToothAdapter();
    }

    private void refreshBlueToothAdapter(){
        this.whcsBlueToothAdapter = BluetoothAdapter.getDefaultAdapter();
    }
}