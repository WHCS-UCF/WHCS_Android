package com.whcs_ucf.whcs_android;

/**
 * Created by Jimmy on 6/8/2015.
 */
public class ControlModule {
    private int identityNumber;
    private ControlModuleRole role;

    public ControlModule(ControlModuleRole role) {
        this.role = role;
    }

    public ControlModuleRole getRole() {
        return role;
    }
}
