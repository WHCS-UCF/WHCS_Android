package com.whcs_ucf.whcs_android;

import android.os.Debug;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class DebugActivity extends WHCSActivity {
    private Button testCommPipelineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        this.testCommPipelineButton = (Button)findViewById(R.id.testCommPipelineButton);
        this.setupListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_debug, menu);
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

    private void setupListeners() {
        this.testCommPipelineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DebugActivity.this.refreshIssuerAndListener();
                DebugActivity.this.whcsIssuer.queueDebugCommand(new ClientCallback() {
                    @Override
                    public void onResponse(WHCSCommand command, WHCSResponse response) {
                        Log.d("WHCS","Successfully completed communication pipeline.");
                    }
                });
            }
        });
    }
}
