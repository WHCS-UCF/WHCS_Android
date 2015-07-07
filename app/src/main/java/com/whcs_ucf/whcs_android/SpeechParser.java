package com.whcs_ucf.whcs_android;

import android.util.Log;

import java.util.List;

/**
 * Created by Jimmy on 7/7/2015.
 */
public class SpeechParser {

    protected static final String[] possibleCommands = new String[] {
        "on",
        "off"
    };

    protected static byte matchVerbToOpcode(String verb) {
        switch (verb) {
            case "on":
                return WHCSOpCodes.TURN_ON_MODULE;
            case "off":
                return WHCSOpCodes.TURN_OFF_MODULE;
            default:
                throw new Error("verb does not represent a valid opcode in speech parser.");
        }
    }

    /**
     *
     * @param speechText
     * @param possibleTargets
     * @return returns null if no valid SpeechCommand could be parsed from the speech text.
     * Otherwise a SpeechCommand is created and returned that contains all the control modules that
     * are targetted by the command.
     */
    public static SpeechCommand parseSpeechTextForCommand(String speechText, List<ControlModule> possibleTargets) {
        speechText = speechText.toLowerCase();
        Log.d("WHCS-UCF", "SPEECHTEXT: "+speechText);
        int indexer;
        for(indexer = 0; indexer < possibleCommands.length; indexer++) {
            if(speechText.contains(possibleCommands[indexer])) {
                Log.d("WHCS-UCF", "found command: " + possibleCommands[indexer]);
                break;
            }
        }
        if(indexer == possibleCommands.length) {
            return null;
        }

        SpeechCommand speechCommand = new SpeechCommand(matchVerbToOpcode(possibleCommands[indexer]));

        for(ControlModule cm : possibleTargets) {
            if(speechText.contains(cm.getName().toLowerCase())) {
                speechCommand.addTarget(cm);
            }
        }

        if(speechCommand.getTargetList() == null || speechCommand.getTargetList().isEmpty()) {
            return null;
        }

        return speechCommand;
    }
}
