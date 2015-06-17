package com.whcs_ucf.whcs_android;

/**
 * Created by Jimmy on 6/8/2015.
 */
public class ControlModule {
    private int identityNumber;
    private ControlModuleRole role;
    private String name;

    public ControlModule(ControlModuleRole role) {
        this.role = role;
    }

    public ControlModuleRole getRole() {
        return role;
    }

    public String getName() {
        if(name == null) {
            return role.getRoleNameRepresentation();
        }
        return name;
    }

    public void setName(String n) {
        name = n;
    }

    public int getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(int n) {
        identityNumber = n;
    }
}
