package com.whcs_ucf.whcs_android;

/**
 * Created by Jimmy on 6/8/2015.
 */
public class ToggleableControlModule extends ControlModule {
    private ToggleableState status;

    public ToggleableControlModule(ControlModuleRole role) {
        super(role);
    }

    public ToggleableState getStatus() {
        return status;
    }

    public static enum ToggleableState {
        ON,
        OFF
    }
}
