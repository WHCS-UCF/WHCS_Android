package com.whcs_ucf.whcs_android;

import android.content.Intent;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class ControlModuleListActivity extends WHCSActivityWithCleanup implements PushFromBaseStationHandler, PipelineErrorHandler {
    protected static final int REQUEST_OK = 1;
    public static final String TAG_BLUETOOTHADDRESS = "bt_address";

    private Button speechButton;
    private Button editButton;
    private ListView controlModuleListView;
    private ControlModuleAdapter controlModuleAdapter;
    private Handler GUIEventLoopHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_module_list);
        Log.d("WHCS-UCF", "Creating ControlModuleListActivity");

        this.GUIEventLoopHandler = new Handler(Looper.getMainLooper());
        this.setupGUI();

        if(!DebugFlags.PREVENT_INITIALIZING_ISSUER_AND_LISTENER) {
            this.refreshIssuerAndListener();
            this.setupEventSubscribers();
            this.getControlModuleStatuses();
        }
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
        setControlModuleAdapterListener();
        populateControlModuleListForInitialDemo();

        setupButtonListeners();
        setupListListener();
    }

    private void setControlModuleAdapterListener() {
        this.controlModuleAdapter.setOnCheckedChangeListenerBeforeAddingModules(new ToggleableControlModuleCheckListener() {
            @Override
            public void onCheckChanged(boolean isChecked, ControlModule cm) {
                byte opCode = WHCSOpCodes.TURN_OFF_MODULE;
                if (isChecked) {
                    opCode = WHCSOpCodes.TURN_ON_MODULE;
                }

                ControlModuleListActivity.this.whcsIssuer.queueCommand(new WHCSCommand(opCode, cm.getIdentityNumber()), new ClientCallback() {
                    @Override
                    public void onResponse(WHCSResponse response) {

                    }
                });
            }
        });
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

    private void setupListListener() {
        this.controlModuleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("WHCS-UCF", "click.");
                startControlModuleDetailsActivity(ControlModuleListActivity.this.controlModuleAdapter.getItem(position));
            }
        });
    }

    private void setupEventSubscribers() {
        this.whcsIssuer.addPushFromBaseStationHandler(this);
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
            SpeechCommand speechCommand = SpeechParser.parseSpeechTextForCommand(sb.toString(), controlModuleAdapter.getControlModules());
            if(speechCommand != null) {
                Toast.makeText(ControlModuleListActivity.this.getApplicationContext(), "Recognized a command. "+speechCommand, Toast.LENGTH_LONG).show();
                for(ControlModule cm : speechCommand.getTargetList()) {
                    ToggleableControlModule.ToggleableState state = (speechCommand.getCommandOpCode() == WHCSOpCodes.TURN_OFF_MODULE) ? ToggleableControlModule.ToggleableState.OFF : ToggleableControlModule.ToggleableState.ON;
                    this.changeStateToggleableControlModule(cm.getIdentityNumber(), state);
                    this.whcsIssuer.queueCommand(new WHCSCommand(speechCommand.getCommandOpCode(), cm.getIdentityNumber()), new ClientCallback() {
                        @Override
                        public void onResponse(WHCSResponse response) {

                        }
                    });
                }
            }
            //Toast.makeText(ControlModuleListActivity.this, "You said " + sb.toString() + ".", Toast.LENGTH_LONG).show();
        }
    }

    private void randomlyPopulateControlModuleList() {
        controlModuleAdapter.clear();
        DatabaseHandler databaseHandler = new DatabaseHandler(this.getApplicationContext());
        for(int i = 0; i < 10; i++) {
            controlModuleAdapter.add(RandomControlModuleGenerator.GenerateRandomizedControlModule(databaseHandler));
        }
    }

    private void populateControlModuleListForInitialDemo() {
        controlModuleAdapter.clear();
        DatabaseHandler databaseHandler = new DatabaseHandler(this.getApplicationContext());
        controlModuleAdapter.add(new ToggleableControlModule(ControlModuleRole.DOOR_CONTROLLER, (byte) 0, databaseHandler));
        controlModuleAdapter.add(new ToggleableControlModule(ControlModuleRole.LIGHT_CONTROLLER, (byte) 1, databaseHandler));
        controlModuleAdapter.add(new DataCollectionControlModule(ControlModuleRole.SENSOR_COLLECTOR, (byte) 2, databaseHandler));
    }

    @Override
    public void onPush(WHCSResponse response) {
        Log.d("WHCS-UCF", "Received push event from base station.");
        routePushResponse(response);
        Log.d("WHCS-UCF", "Routed push event according to ControlModuleListActivity's design.");
    }

    private void routePushResponse(WHCSResponse response) {
        switch(response.getOpcode()) {
            case WHCSOpCodes.CONTROL_MODULES_CHANGED:
                //This is the case for CONTROL_MODULES_CHANGED
                getControlModuleStatuses();
                break;
            default:
                break;
        }
    }

    private void startControlModuleDetailsActivity(ControlModule cm) {
        Intent intent = new Intent(this.getApplicationContext(), ControlModuleDetailsActivity.class);
        intent.putExtra(ControlModuleDetailsActivity.TAG_CONTROLMODULE, cm);
        this.startActivity(intent);
    }


    private class ToggleUpdater implements Runnable {
        @Override
        public void run() {
            if(controlModuleAdapter == null)
                return;
            if(controlModuleAdapter.isEmpty())
                return;
            ControlModule cm = controlModuleAdapter.getItem(0);
            if(cm instanceof ToggleableControlModule) {
                ToggleableControlModule tcm = (ToggleableControlModule) cm;
                tcm.toggle();
            }
            controlModuleAdapter.notifyDataSetChanged();
        }
    }

    private void toggleToggleableControlModule(byte identityNumber) {
        ControlModule target = getControlModuleFromIdentityNumber(identityNumber);
        if( target == null ) return;
        if(target instanceof  ToggleableControlModule) {
            ((ToggleableControlModule)target).toggle();
            this.controlModuleAdapter.notifyDataSetChanged();
        }
    }

    private void changeStateToggleableControlModule(byte identityNumber, ToggleableControlModule.ToggleableState state) {
        ControlModule target = getControlModuleFromIdentityNumber(identityNumber);
        if( target == null ) return;
        if(target instanceof  ToggleableControlModule) {
            ((ToggleableControlModule)target).setStatus(state);
            this.controlModuleAdapter.notifyDataSetChanged();
        }
    }

    private ControlModule getControlModuleFromIdentityNumber(byte identityNumber) {
        int indexer;
        for(indexer = 0; indexer < controlModuleAdapter.getCount(); indexer++) {
            if(controlModuleAdapter.getItem(indexer).getIdentityNumber() == identityNumber) {
                return controlModuleAdapter.getItem(indexer);
            }
        }
        return null;
    }

    private void getControlModuleStatuses() {
        for (int i = 0; i < controlModuleAdapter.getCount(); i++) {
            this.whcsIssuer.queueCommand(new WHCSCommand(WHCSOpCodes.GET_MODULE_STATUS, (byte) i), new ClientCallback() {
                @Override
                public void onResponse(WHCSResponse response) {
                    ControlModuleListActivity.this.controlModuleAdapter.getItem(response.getControlTarget()).updateStatus(response.getResponseByte());
                    ControlModuleListActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ControlModuleListActivity.this.controlModuleAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        DatabaseHandler databaseHandler = new DatabaseHandler(this.getApplicationContext());
        for(int i = 0; i < this.controlModuleAdapter.getCount(); i++) {
            this.controlModuleAdapter.getItem(i).refreshName(databaseHandler);
        }
        this.controlModuleAdapter.notifyDataSetChanged();
    }
}
