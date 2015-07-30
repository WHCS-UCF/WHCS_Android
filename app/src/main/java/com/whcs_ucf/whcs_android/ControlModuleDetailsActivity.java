package com.whcs_ucf.whcs_android;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;


public class ControlModuleDetailsActivity extends WHCSActivity {
    /**
     * Used for passing a control module to the control module details activity. This is the string
     * that should be used to paired with the control module parcelable in the intent.
     */
    public static final String TAG_CONTROLMODULE = "control_module";

    private Button saveButton;
    private Button discardButton;
    private EditText changeNameEditText;
    private TextView roleIndicatorTextView;
    private TextView controlModuleTextView;
    private TextView statusIndicatorTextView;
    private CheckBoxGroup checkBoxGroup;
    private ControlModule underlyingControlModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_module_details);

        setupGUIVariables();
        this.underlyingControlModule = this.getIntent().getParcelableExtra(TAG_CONTROLMODULE);
        if(this.underlyingControlModule instanceof  ToggleableControlModule) {
            Log.d("WHCS-UCF", "detail view for toggleable: "+ underlyingControlModule.toString());
        }
        else if(this.underlyingControlModule instanceof DataCollectionControlModule) {
            Log.d("WHCS-UCF", "detail view for datacollection");
        }

        setupGUIForControlModule();
        setupGUIEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_control_module_details, menu);
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
        this.saveButton = (Button) this.findViewById(R.id.saveButton);
        this.discardButton = (Button) this.findViewById(R.id.discardButton);
        this.roleIndicatorTextView = (TextView) this.findViewById(R.id.roleIndicatorTextView);
        this.changeNameEditText = (EditText) this.findViewById(R.id.changeNameEditText);
        this.controlModuleTextView = (TextView) this.findViewById(R.id.controlModuleTextView);
        this.statusIndicatorTextView = (TextView) this.findViewById(R.id.statusIndicatorTextView);
    }

    private void setupCheckBoxGroup() {
        checkBoxGroup = new CheckBoxGroup();
        checkBoxGroup.add( (CheckBox) this.findViewById(R.id.checkBox1) );
        checkBoxGroup.add( (CheckBox) this.findViewById(R.id.checkBox2) );
        checkBoxGroup.add( (CheckBox) this.findViewById(R.id.checkBox3) );
        checkBoxGroup.add( (CheckBox) this.findViewById(R.id.checkBox4) );
        checkBoxGroup.add( (CheckBox) this.findViewById(R.id.checkBox5) );
        checkBoxGroup.add( (CheckBox) this.findViewById(R.id.checkBox6) );

        DatabaseHandler dbHandler = new DatabaseHandler(this.getApplicationContext());
        List<ControlModuleGrouping> cmGroupings = dbHandler.getSpecificControlModulesGroupings(this.underlyingControlModule);
        if(cmGroupings != null) {
            for(ControlModuleGrouping grouping : cmGroupings) {
                // groupNumber - 1 because groupNumbers start at 1 but arraylist indexing starts
                // at 0.
                checkBoxGroup.get(grouping.getGroupNumber() - 1).setChecked(true);
            }
        }
        checkBoxGroup.recordInitialState();
    }

    private void setupGUIForControlModule() {
        this.controlModuleTextView.setText(this.underlyingControlModule.getName());
        this.statusIndicatorTextView.setText(this.underlyingControlModule.statusableGetString());
        this.roleIndicatorTextView.setText(this.underlyingControlModule.getRole().name());
        this.changeNameEditText.setText(this.underlyingControlModule.getName());
        this.setupCheckBoxGroup();
    }

    private void setupGUIEvents() {
        this.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler dbHandler = new DatabaseHandler(ControlModuleDetailsActivity.this.getApplicationContext());
                if(!dbHandler.controlModuleExists(underlyingControlModule)) {
                    dbHandler.addControlModule(underlyingControlModule);
                }
                if(!(ControlModuleDetailsActivity.this.underlyingControlModule.getName().equals(ControlModuleDetailsActivity.this.changeNameEditText.getText().toString()))) {
                    Log.d("WHCS-UCF", "Changing name of Control Module: " + underlyingControlModule + " to: "+ changeNameEditText.getText().toString());
                    ControlModuleDetailsActivity.this.underlyingControlModule.setName(changeNameEditText.getText().toString());
                    dbHandler.updateControlModule(underlyingControlModule);
                }
                if(checkBoxGroup.stateHasChanged()) {
                    dbHandler.updateControlModulesGroupings(underlyingControlModule, checkBoxGroup.getCheckedIndexList(1));
                }

                finish();
            }
        });
    }
}
