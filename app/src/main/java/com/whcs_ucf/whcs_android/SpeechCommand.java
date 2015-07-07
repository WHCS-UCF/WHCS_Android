package com.whcs_ucf.whcs_android;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jimmy on 7/7/2015.
 */
public class SpeechCommand {

    private List<ControlModule> targetList;
    private byte command;

    public SpeechCommand(byte b) {
        this.command = b;
    }

    public List<ControlModule> getTargetList() {
        return targetList;
    }

    public void addTarget(ControlModule cm) {
        if(this.targetList == null) {
            targetList = new ArrayList<ControlModule>();
        }
        targetList.add(cm);
    }

    public byte getCommandOpCode() {
        return command;
    }

    public void setCommandOpCode(byte b) {
        this.command = b;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SpeechCommand | targets: ");
        for(ControlModule cm : targetList) {
            sb.append(cm.getName());
            sb.append(" ");
        }
        sb.append(", command: ");
        sb.append(command);
        sb.append(".");
        return sb.toString();
    }
}
