package com.whcs_ucf.whcs_android;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;


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

    private void setupGUIForControlModule() {
        this.controlModuleTextView.setText(this.underlyingControlModule.getName());
        this.statusIndicatorTextView.setText(this.underlyingControlModule.statusableGetString());
        this.roleIndicatorTextView.setText(this.underlyingControlModule.getRole().name());
    }

    private void setupGUIEvents() {
        this.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ControlModuleDetailsActivity.this.underlyingControlModule.getName().equals(ControlModuleDetailsActivity.this.changeNameEditText.getText().toString())) {
                    return;
                }
                Log.d("WHCS-UCF", "Changing name of Control Module: " + underlyingControlModule + " to: "+ changeNameEditText.getText().toString());
                ControlModuleDetailsActivity.this.underlyingControlModule.setName(changeNameEditText.getText().toString());
                ControlModule checkIfExistsCM;
                DatabaseHandler dbHandler = new DatabaseHandler(ControlModuleDetailsActivity.this.getApplicationContext());
                checkIfExistsCM = dbHandler.getControlModule(underlyingControlModule.getIdentityNumber());
                if(checkIfExistsCM == null) {
                    dbHandler.addControlModule(underlyingControlModule);
                }
                else {
                    dbHandler.updateControlModule(underlyingControlModule);
                }
                finish();
            }
        });
    }
}
