package com.whcs_ucf.whcs_android;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Jimmy on 6/15/2015.
 */
public class WHCSResponse {
    public static final int MAX_RESPONSE_SIZE = 100;
    /*
     * Response structure:
     * start bit 0x1B
     * |  reference ID  |  Op Code  | Control Module Target  |  [length  |  ...value bytes... |  ] / [Response Value  |  ]
     */
    private byte refId;
    private byte opCode;
    private byte controlTarget;
    private ResponseType responseType;

    /*
     * Depending on the type of the response one of these will be filled.
     * If the response is a string response the responseString field will be filled
     * with the string that is returned in the response.
     */
    private String responseString;
    private byte responseByte;

    public WHCSResponse(byte refId, byte opCode, byte controlTarget) {
        this.refId = refId;
        this.opCode = opCode;
        this.controlTarget = controlTarget;
        this.responseType = ResponseType.NO_RESULT;
    }

    public WHCSResponse(byte refId, byte opCode, byte controlTarget, byte responseByte) {
        this.refId = refId;
        this.opCode = opCode;
        this.controlTarget = controlTarget;
        this.responseByte = responseByte;
        this.responseType = ResponseType.BYTE_RESULT;
    }

    public WHCSResponse(byte refId, byte opCode, byte controlTarget, String responseString) {
        this.refId = refId;
        this.opCode = opCode;
        this.controlTarget = controlTarget;
        this.responseString = responseString;
        this.responseType = ResponseType.VARIABLE_SIZE_RESULT;
    }

    public static WHCSResponse parseResponse(Byte byteArr[]) {
        if(byteArr.length < 3) {
            throw new Error("A response must be at least 3 bytes.");
        }
        if(byteArr.length == 3) {
            return new WHCSResponse(byteArr[0], byteArr[1], byteArr[2]);
        }
        else if(byteArr.length == 4) {
            return new WHCSResponse(byteArr[0], byteArr[1], byteArr[2], byteArr[3]);
        }
        else {
            byte variableResponseLength = byteArr[3];
            char[] responseCharArray = new char[variableResponseLength];
            for(int i = 0; i < variableResponseLength; i ++) {
                responseCharArray[i] = (char)((byte)(byteArr[4+i]));
            }
            return new WHCSResponse(byteArr[0], byteArr[1], byteArr[2], String.valueOf(responseCharArray));
        }
    }

    public static WHCSResponse parseResponse(ArrayList<Byte> byteList) {
        return parseResponse(byteList.toArray(new Byte[byteList.size()]));
    }

    public byte getRefId() {
        return this.refId;
    }

    public byte getControlTarget() {
        return this.controlTarget;
    }

    public byte getResponseByte() {
        if(this.responseType != ResponseType.BYTE_RESULT) {
            throw new Error("Trying to get byte result from WHCSResponse that is not BYTE_RESULT.");
        }

        return this.responseByte;
    }

    public enum ResponseType {
        NO_RESULT,
        BYTE_RESULT,
        VARIABLE_SIZE_RESULT
    }

    public static class WHCSResponseParser{
        /*
         * The ResponseParser allows any class to add data to a black box and check if it has successfully constructed
         * a valid WHCSResponse.
         * In the architecture of WHCS this is useful because the BluetoothListener can continuously add bytes
         * to the parser and check if a response has been constructed. The response can then be extracted using
         * getResponseFromCompletedParser and then it can be handed over to the command issuer.
         *
         * The command parser can be reused with its reset() functionality.
         */
        private boolean completed;
        //Responses from the base station all start with 0x1B. We don't want to parse any response data
        //Until this specific byte has been reached.
        private boolean receivedStartByte;
        private int newDataIndex;
        private byte[] responseArray = new byte[WHCSResponse.MAX_RESPONSE_SIZE];

        public WHCSResponseParser() {
            this.completed = false;
            this.receivedStartByte = false;
            this.newDataIndex = 0;
        }

        public void addData(byte[] data, int numBytes, ResponseHandler responseHandler) {
            for(int i = 0; i < numBytes; i++) {
                if((!this.receivedStartByte) && (data[i] != 0x1B)) {
                    continue;
                }
                else if(!this.receivedStartByte) {
                    receivedStartByte = true;
                }
                else if(newDataIndex < WHCSResponse.MAX_RESPONSE_SIZE) {
                    responseArray[newDataIndex] = data[i];
                    newDataIndex++;
                    completed = checkIfResponseArrayComplete();
                    if(completed) {
                        responseHandler.handleResponse(getResponseFromCompletedParser());
                        reset();
                    }
                }
            }
        }

        private boolean checkIfResponseArrayComplete() {
            if(newDataIndex < 3) {
                return false;
            }
            if((WHCSOpCodes.getWHCSResponseTypeFromOpCode(responseArray[1]) == ResponseType.NO_RESULT)) {
                return true;
            }
            else if((WHCSOpCodes.getWHCSResponseTypeFromOpCode(responseArray[1]) == ResponseType.BYTE_RESULT)) {
                if(newDataIndex >= 4) {
                    return true;
                }
            }
            else if(WHCSOpCodes.getWHCSResponseTypeFromOpCode(responseArray[1]) == ResponseType.VARIABLE_SIZE_RESULT) {
                if(newDataIndex < 4) {
                    return false;
                }
                byte variableResponseLength = responseArray[3];
                if(newDataIndex == 4 + variableResponseLength) {
                    return true;
                }
                else return false;
            }
            return false;
        }

        public WHCSResponse getResponseFromCompletedParser() {
            if(!completed) {
                throw new Error("Can't get a response if the parser isn't finished yet.");
            }

            if(newDataIndex < 3) {
                throw new Error("A response must be at least 3 bytes.");
            }
            if(WHCSOpCodes.getWHCSResponseTypeFromOpCode(responseArray[1]) == ResponseType.NO_RESULT) {
                return new WHCSResponse(responseArray[0], responseArray[1], responseArray[2]);
            }
            else if(WHCSOpCodes.getWHCSResponseTypeFromOpCode(responseArray[1]) == ResponseType.BYTE_RESULT) {
                return new WHCSResponse(responseArray[0], responseArray[1], responseArray[2], responseArray[3]);
            }
            else {
                byte variableResponseLength = responseArray[3];
                char[] responseCharArray = new char[variableResponseLength];
                for(int i = 0; i < variableResponseLength; i ++) {
                    responseCharArray[i] = (char)((byte)(responseArray[4+i]));
                }
                return new WHCSResponse(responseArray[0], responseArray[1], responseArray[2], String.valueOf(responseCharArray));
            }
        }

        public boolean hasCompletelyParsed() {
            return completed;
        }

        public void reset() {
            this.completed = false;
            this.newDataIndex = 0;
            this.receivedStartByte = false;
        }
    }
}
