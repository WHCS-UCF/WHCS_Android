package com.whcs_ucf.whcs_android;

import android.bluetooth.BluetoothDevice;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class ControlModuleListActivity extends WHCSActivity {
    public static final String TAG_MAC_STRING = "macString";
    private BluetoothDevice baseStationDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_module_list);

        String macString = getIntent().getStringExtra(TAG_MAC_STRING);
        if(macString != null) {
            initializeActivity(macString);
        }
    }

    private void initializeActivity(String macString) {
        baseStationDevice = whcsBlueToothAdapter.getRemoteDevice(macString);

        WHCSBaseStationClient client = new WHCSBaseStationClient();
        client.performCommand(new ClientCallback() {
            @Override
            public void onResponse() {
                Log.d("WHCS", "testing callback.");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_control_module_list, menu);
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
}
