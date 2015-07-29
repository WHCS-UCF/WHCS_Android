package com.whcs_ucf.whcs_android;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Jimmy on 7/28/2015.
 */
public class ToggleableButton extends Button {
    private boolean isOn = false;
    public ToggleableButton(Context context) {
        super(context);
    }

    public ToggleableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToggleableButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void toggle() {
        isOn = !isOn;
    }

    public boolean getIsOn() {
        return isOn;
    }

    public void turnOff() {
        isOn = false;
    }

    public void turnOn() {
        isOn = true;
    }
}
