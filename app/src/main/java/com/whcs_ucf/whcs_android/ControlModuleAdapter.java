package com.whcs_ucf.whcs_android;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jimmy on 6/17/2015.
 */
public class ControlModuleAdapter extends ArrayAdapter<ControlModule>{
    private ToggleableControlModuleCheckListener onCheckedChangeListener;

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

        final ControlModule controlModule = getItem(position);

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

                if(this.onCheckedChangeListener != null) {
                    toggleableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (ControlModuleAdapter.this.onCheckedChangeListener != null) {
                                Log.d("WHCS-UCF", "toggled a switch.");
                                onCheckedChangeListener.onCheckChanged(isChecked, controlModule);
                            }
                        }
                    });
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

    public ArrayList<ControlModule> getControlModules() {
        ArrayList<ControlModule> list = new ArrayList<ControlModule>();
        for(int i = 0; i < this.getCount(); i++) {
            list.add(this.getItem(i));
        }
        return list;
    }

    public void setOnCheckedChangeListenerBeforeAddingModules(ToggleableControlModuleCheckListener listener) {
        this.onCheckedChangeListener = listener;
    }
}
