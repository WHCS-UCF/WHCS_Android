package com.whcs_ucf.whcs_android;

/**
 * Created by Jimmy on 6/15/2015.
 */
public class CommandCallbackPair implements Comparable<CommandCallbackPair> {
    private WHCSCommand command;
    private ClientCallback callBack;

    public CommandCallbackPair(WHCSCommand command, ClientCallback cb) {

    }

    public WHCSCommand getCommand() {
        return command;
    }

    public ClientCallback getCallback() {
        return callBack;
    }

    @Override
    public int compareTo(CommandCallbackPair another) {
        return another.getCommand().compareTo(this.getCommand());
    }
}
