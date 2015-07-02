package com.whcs_ucf.whcs_android;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class ControlModuleDetailsActivity extends WHCSActivity {
    /**
     * Used for passing a control module to the control module details activity. This is the string
     * that should be used to paired with the control module parcelable in the intent.
     */
    public static final String TAG_CONTROLMODULE = "control_module";

    private TextView controlModuleTextView;
    private ControlModule underlyingControlModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_module_details);

        setupGUIVariables();
        this.underlyingControlModule = this.getIntent().getParcelableExtra(TAG_CONTROLMODULE);

        setupGUIForControlModule();
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
        this.controlModuleTextView = (TextView) this.findViewById(R.id.controlModuleTextView);
    }

    private void setupGUIForControlModule() {
        this.controlModuleTextView.setText(this.underlyingControlModule.getName());
    }
}
