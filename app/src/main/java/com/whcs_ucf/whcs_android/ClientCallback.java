package com.whcs_ucf.whcs_android;

/**
 * Created by Jimmy on 6/8/2015.
 * onResponse is called when the CommandIssuer is given a response from the BluetoothListener
 * onTimeOut is called if no response is received for an issued command within the specified
 * timeout period. This timeout period is specified in the CommandIssuer.
 * onSentOut is called as soon as the command is given to the CommandSender from the
 * CommandIssuer.
 */
public abstract class ClientCallback {

    public abstract void onResponse(WHCSCommand command, WHCSResponse response);

    public void onTimeOut(WHCSCommand timedOutCommand) {}

    public void onSentOut() {}
}
