package com.whcs_ucf.whcs_android;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;


public class BaseStationConnectActivity extends WHCSActivity {
    private ListView pairedList;
    private ListView activeList;
    private Button pairedbutton;
    private Button activeButton;

    private ArrayAdapter<String> pairedArrayAdapter;
    private ArrayAdapter<String> activeArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_station_connect);

        //establishes refs to buttons, lists
        this.setupGUIVariables();
        //sets event listeners for buttons
        this.setupButtonListeners();
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

        activeArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        activeList.setAdapter(activeArrayAdapter);

        //Establishes the references to the buttons in the basestation connection activity.
        this.pairedbutton = (Button)this.findViewById(R.id.pairedButton);
        this.activeButton = (Button)this.findViewById(R.id.activeButton);
    }

    private void setupButtonListeners() {
        pairedbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedArrayAdapter.clear();
                for(int i=0; i<5; i++){
                    pairedArrayAdapter.add("Device number #" + i);
                }
            }
        });

        activeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeArrayAdapter.clear();
                for(int i=0; i<5; i++){
                    activeArrayAdapter.add("Device number #" + i);
                }
            }
        });
    }

    private void ListPairedDevices() {

        // get paired devices

        Set<BluetoothDevice> pairedDevices = whcsBlueToothAdapter.getBondedDevices();

        //Clear everything currently in the list's adapter
        pairedArrayAdapter.clear();

        // put it's one to the adapter
        for(BluetoothDevice device : pairedDevices)
            pairedArrayAdapter.add(device.getName()+ "\n" + device.getAddress());

        Toast.makeText(this.getApplicationContext(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
    }
}
