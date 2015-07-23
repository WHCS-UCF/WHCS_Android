package com.whcs_ucf.whcs_android;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Jimmy on 6/15/2015.
 */
public class CommandIssuer implements Runnable, ResponseHandler {

    /*
     * Command Issuer is a middleman between the application and the BluetoothListener which directly interacts with sockets.
     * Commands are queued into the command issuer and the issuer handles dispatching these commands to the listener. The issuer
     * handles getting responses back from the listener and routing them back to the appropriate callbacks.
     *
     * CommandIssuer is a singleton class. There should only be one in existence at all times while using the WHCS application.
     * In order to get the singleton instance of CommandIssuer use getSingletongCommandIssuer()
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
    private final int timeoutLength = 10000; //measured in milliseconds

    private boolean commandIsOutgoing;
    private long lastCommandIssueTime;
    private LinkedList<CommandCallbackPair> outstandingCommands;
    private CommandCallbackPair currentCommand;
    private CommandSender commandSender;
    private PipelineErrorHandler pipelineErrorHandler;
    private ArrayList<PushFromBaseStationHandler> pushHandlerList;
    //Used to tell the issuer when to stop. It always checks if it needs to stop.
    private boolean shouldStop;

    private static CommandIssuer SingletonCommandIssuer;
    private static Thread IssuerThread;

    private CommandIssuer() {
        this.commandIsOutgoing = false;
        this.outstandingCommands = new LinkedList<CommandCallbackPair>();
        this.pushHandlerList = new ArrayList<PushFromBaseStationHandler>();
    }

    public static CommandIssuer GetSingletonCommandIssuer() {
        if(SingletonCommandIssuer == null) {
            SingletonCommandIssuer = new CommandIssuer();
            IssuerThread = new Thread(SingletonCommandIssuer);
            if(!IssuerThread.isAlive()) {
                IssuerThread.start();
            }

        }
        return SingletonCommandIssuer;
    }

    @Override
    public void run() {
        while(true) {
            if (shouldStop) {
                return;
            }
            if (!this.commandIsOutgoing) {
                if (!this.outstandingCommands.isEmpty()) {
                    Log.d("WHCS-UCF", "Thread ID: " + Thread.currentThread().getId() +" is now issuing a command in the CommandIssuer.");
                    this.issueCommandCallbackPair();
                }
            }

            if (this.commandIsOutgoing && ((System.currentTimeMillis() - this.lastCommandIssueTime) > this.timeoutLength)) {
                Log.d("WHCS-UCF", "Issuer is timing out a command.");
                timeoutCurrentCommand();
            }
        }
    }

    synchronized
    private void issueCommandCallbackPair() {
        if (!this.outstandingCommands.isEmpty()) {
            if(this.commandSender == null) {
                throw new Error("Can't issue commands if the CommandIssuer doesn't have a CommandSender.");
            }
            this.currentCommand = this.outstandingCommands.poll();
            this.commandIsOutgoing = true;
            this.lastCommandIssueTime = System.currentTimeMillis();
            this.currentCommand.getCallback().onSentOut();

            try {
                this.commandSender.sendOutCommand(this.currentCommand.getCommand());
            } catch (Exception e) {
                e.printStackTrace();
                if(this.pipelineErrorHandler != null) {
                    this.shouldStop = true;
                    this.pipelineErrorHandler.onCommunicationPipelineError();
                }
            }
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

    public void queueDebugCommand(ClientCallback cb) {
        if(this.commandSender == null) {
            throw new Error("Can't issue commands if the CommandIssuer doesn't have a CommandSender.");
        }
        WHCSCommand debugCommand = WHCSCommand.CreateGetBaseStationStatusDEBUGCommand();
        this.currentCommand = new CommandCallbackPair(debugCommand, cb);
        this.commandIsOutgoing = true;
        this.lastCommandIssueTime = System.currentTimeMillis();
        this.currentCommand.getCallback().onSentOut();

        try {
            this.commandSender.sendOutCommand(debugCommand);
        } catch (Exception e) {
            this.shouldStop = true;
            this.pipelineErrorHandler.onCommunicationPipelineError();
            e.printStackTrace();
        }
    }

    public void queueQueryBaseStationCommand(ClientCallback cb) {
        if(this.commandSender == null) {
            throw new Error("Can't issue commands if the CommandIssuer doesn't have a CommandSender.");
        }
        WHCSCommand debugCommand = WHCSCommand.CreateQueryIfBaseStationCommand();
        this.currentCommand = new CommandCallbackPair(debugCommand, cb);
        this.commandIsOutgoing = true;
        this.lastCommandIssueTime = System.currentTimeMillis();
        this.currentCommand.getCallback().onSentOut();

        try {
            this.commandSender.sendOutCommand(debugCommand);
        } catch (Exception e) {
            this.shouldStop = true;
            this.pipelineErrorHandler.onCommunicationPipelineError();
            e.printStackTrace();
        }
    }

    public void handleResponse(WHCSResponse response) {
        if(currentCommand != null && response.getRefId() == currentCommand.getCommand().getRefId()) {
            this.currentCommand.getCallback().onResponse(response);
            this.currentCommand = null;
            this.commandIsOutgoing = false;
        }
        else if(response.getRefId() == 0x00) {
            for(PushFromBaseStationHandler handler : pushHandlerList) {
                handler.onPush(response);
            }
        }
    }

    public void stop() {
        this.shouldStop = true;
        this.SingletonCommandIssuer = null;
    }

    public void setPipelineErrorHandler(PipelineErrorHandler pipelineErrorHandler) {
        this.pipelineErrorHandler = pipelineErrorHandler;
    }

    public void setCommandSender(CommandSender sender) {
        this.commandSender = sender;
    }

    public void addPushFromBaseStationHandler(PushFromBaseStationHandler handler) {
        this.pushHandlerList.add(handler);
    }
}
