package com.whcs_ucf.whcs_android;

import android.os.Looper;

import java.util.LinkedList;

/**
 * Created by Jimmy on 6/15/2015.
 */
public class CommandIssuer implements Runnable {

    /*
     * Command Issuer is a middleman between the application and the BluetoothListener which directly interacts with sockets.
     * Commands are queued into the command issuer and the issuer handles dispatching these commands to the listener. The issuer
     * knows handles getting responses back from the listener and routing them back to the appropriate callbacks.
     *
     * Example
     *
     * issuer.queueCommand(someCommand, new ClientCallback() {
     *  @Override
     *   public void onResponse(WHCSResponse response) {
     *     Log.d("WHCS", "command went through to the base station and came back.");
     *   }
     * }
     */

    // Timeout length for how long the base station should have to respond to a command from Android
    private int timeoutLength;

    private boolean commandIsOutgoing;
    private long lastCommandIssueTime;
    private LinkedList<CommandCallbackPair> outstandingCommands;
    private CommandCallbackPair currentCommand;

    public CommandIssuer() {
        this.commandIsOutgoing = false;
        this.timeoutLength = 10000; // measured in milliseconds.
        this.outstandingCommands = new LinkedList<CommandCallbackPair>();
    }

    @Override
    public void run() {
        if(!this.commandIsOutgoing) {
            if(!this.outstandingCommands.isEmpty()) {
                this.issueCommandCallbackPair();
            }
        }

        if(this.commandIsOutgoing && ((System.currentTimeMillis() - this.lastCommandIssueTime) > this.timeoutLength)) {
            timeoutCurrentCommand();
        }
    }

    private void issueCommandCallbackPair() {
        if (!this.outstandingCommands.isEmpty()) {
            this.currentCommand = this.outstandingCommands.poll();
            this.commandIsOutgoing = true;
            this.lastCommandIssueTime = System.currentTimeMillis();
            this.currentCommand.getCallback().onSentOut();
        }
    }

    private void timeoutCurrentCommand() {
        this.currentCommand.getCallback().onTimeOut();
        this.currentCommand = null;
        this.commandIsOutgoing = false;
    }

    public void queueCommand(WHCSCommand command, ClientCallback cb) {
        this.outstandingCommands.add(new CommandCallbackPair(command, cb));
    }

    public void handleResponse(WHCSResponse response) {
        if(response.getRefId() == currentCommand.getCommand().getRefId()) {
            this.currentCommand.getCallback().onResponse(response);
            this.currentCommand = null;
            this.commandIsOutgoing = false;
        }
        else {
            //This needs to handle pushes from the base station. The push won't have a reference to an issued command.
        }
    }
}
