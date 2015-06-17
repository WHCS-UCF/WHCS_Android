package com.whcs_ucf.whcs_android;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.ResourceBundle;


public class ControlModuleListActivity extends WHCSActivity {
    protected static final int REQUEST_OK = 1;

    private Button speechButton;
    private Button editButton;
    private ListView controlModuleListView;
    private ControlModuleAdapter controlModuleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_module_list);

        setupGUI();
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

    private void setupGUI() {
        this.speechButton = (Button)findViewById(R.id.speechButton);
        this.editButton = (Button)findViewById(R.id.editButton);
        this.controlModuleListView = (ListView)findViewById(R.id.controlModulesListView);

        this.controlModuleAdapter = new ControlModuleAdapter(this, R.layout.control_module_list_row);
        this.controlModuleListView.setAdapter(controlModuleAdapter);
        randomlyPopulateControlModuleList();

        setupButtonListeners();
    }

    private void setupButtonListeners() {
        this.speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                try {
                    startActivityForResult(i, REQUEST_OK);
                } catch (Exception e) {
                    Toast.makeText(ControlModuleListActivity.this, "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
                }
            }
        });
        this.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_OK  && resultCode==RESULT_OK) {
            ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            StringBuilder sb = new StringBuilder();
            for(String s : thingsYouSaid) {
                sb.append(s);
                sb.append(" ");
            }
            sb.deleteCharAt(sb.length()-1);
            Toast.makeText(ControlModuleListActivity.this, "You said " + sb.toString() + ".", Toast.LENGTH_LONG).show();
        }
    }

    private void randomlyPopulateControlModuleList() {
        controlModuleAdapter.clear();
        for(int i = 0; i < 10; i++) {
            controlModuleAdapter.add(new ControlModule(ControlModuleRole.GetRandomControlModuleRole()));
        }
    }
}
