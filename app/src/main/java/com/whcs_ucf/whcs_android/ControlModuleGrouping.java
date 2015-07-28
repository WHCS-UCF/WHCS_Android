package com.whcs_ucf.whcs_android;

/**
 * Created by Jimmy on 7/27/2015.
 */
public class ControlModuleGrouping {

    private byte controlModuleId;
    private int groupNumber;

    ControlModuleGrouping(byte controlModuleId, int groupNumber) {
        this.controlModuleId = controlModuleId;
        this.groupNumber = groupNumber;
    }

    public void setControlModuleId(byte controlModuleId) {
        this.controlModuleId = controlModuleId;
    }

    public byte getControlModuleId() {
        return controlModuleId;
    }

    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }

    public int getGroupNumber() {
        return groupNumber;
    }

    @Override
    public String toString() {
        return "Group " + groupNumber +", Control Module " + controlModuleId;
    }
}
