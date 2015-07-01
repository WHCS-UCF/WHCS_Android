package com.whcs_ucf.whcs_android;

/**
 * Created by Jimmy on 6/8/2015.
 */
public class ToggleableControlModule extends ControlModule {
    private ToggleableState status;

    public ToggleableControlModule(ControlModuleRole role) {
        super(role);
    }

    public ToggleableState toggle() {
        if(status == ToggleableState.OFF) {
            status = ToggleableState.ON;
        }
        else {
            status = ToggleableState.OFF;
        }
        return status;
    }

    public ToggleableState getStatus() {
        return status;
    }

    public enum ToggleableState {
        ON,
        OFF
    }
}
