package com.whcs_ucf.whcs_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Jimmy on 6/17/2015.
 */
public class ControlModuleAdapter extends ArrayAdapter<ControlModule>{
    public ControlModuleAdapter(Context context, int resource) {
        super(context, resource);
    }

    public ControlModuleAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public ControlModuleAdapter(Context context, int resource, ControlModule[] objects) {
        super(context, resource, objects);
    }

    public ControlModuleAdapter(Context context, int resource, int textViewResourceId, ControlModule[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public ControlModuleAdapter(Context context, int resource, List<ControlModule> objects) {
        super(context, resource, objects);
    }

    public ControlModuleAdapter(Context context, int resource, int textViewResourceId, List<ControlModule> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ControlModule controlModule = getItem(position);

        View v = null;

        LayoutInflater vi;
        vi = LayoutInflater.from(getContext());



        if (controlModule != null) {
            if(controlModule instanceof ToggleableControlModule) {
                v = vi.inflate(R.layout.control_module_list_row, null);

                TextView text1 = (TextView) v.findViewById(R.id.cmText1);
                Switch toggleableSwitch = (Switch) v.findViewById(R.id.cmSwitch);

                if (text1 != null) {
                    text1.setText(controlModule.getName());
                }
                ToggleableControlModule togControlModule = (ToggleableControlModule) controlModule;
                if(togControlModule.getStatus() == ToggleableControlModule.ToggleableState.ON) {
                    toggleableSwitch.setChecked(true);
                }
                else {
                    toggleableSwitch.setChecked(false);
                }
            }
            else if(controlModule instanceof DataCollectionControlModule) {
                v = vi.inflate(R.layout.sensor_control_module_list_row, null);

                TextView sensorCMText1 = (TextView) v.findViewById(R.id.sensorCMText1);
                TextView sensorCMText2 = (TextView) v.findViewById(R.id.sensorCMText2);

                DataCollectionControlModule dcm = (DataCollectionControlModule) controlModule;

                if (sensorCMText1 != null) {
                    sensorCMText1.setText(controlModule.getName());
                }

                if (sensorCMText2 != null) {
                    sensorCMText2.setText(Byte.toString(dcm.getSensorValue()));
                }
            }
            else {
                v = vi.inflate(R.layout.control_module_list_row, null);

                TextView text1 = (TextView) v.findViewById(R.id.cmText1);
                Switch toggleableSwitch = (Switch) v.findViewById(R.id.cmSwitch);

                if (text1 != null) {
                    text1.setText(controlModule.getName());
                }
            }
        }

        return v;
    }
}
