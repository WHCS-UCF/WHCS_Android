package com.whcs_ucf.whcs_android;

/**
 * Created by Jimmy on 6/15/2015.
 * The CommandCallbackPair class is used to keep commands and their callbacks close
 * together in the code in which they are used. Often a command's callback will need to be
 * used directly after the command is dealt with, and the correct callback will need to be
 * found for the command. The CommandCallbackPair makes it easy to keep track of the correct
 * callback.
 */
public class CommandCallbackPair implements Comparable<CommandCallbackPair> {
    private WHCSCommand command;
    private ClientCallback callBack;

    public CommandCallbackPair(WHCSCommand command, ClientCallback cb) {
        this.command = command;
        this.callBack = cb;
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
