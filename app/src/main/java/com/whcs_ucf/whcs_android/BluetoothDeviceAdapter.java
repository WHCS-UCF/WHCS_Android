package com.whcs_ucf.whcs_android;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Jimmy on 6/8/2015.
 */
public class BluetoothDeviceAdapter extends ArrayAdapter<BluetoothDevice> {

    public BluetoothDeviceAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public BluetoothDeviceAdapter(Context context, int resource, List<BluetoothDevice> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(android.R.layout.simple_list_item_1, null);
        }

        BluetoothDevice btDevice = getItem(position);

        if (btDevice != null) {
            TextView text1 = (TextView) v.findViewById(android.R.id.text1);

            if (text1 != null) {
                text1.setText(stringifyBluetoothDevice(btDevice));
            }
        }

        return v;
    }

    public String stringifyBluetoothDevice(BluetoothDevice Device) {
        return Device.getName()+ "\n" + Device.getAddress();
    }
}