package com.whcs_ucf.whcs_android;

import java.util.ArrayList;

/**
 * Created by Jimmy on 6/15/2015.
 */
public class WHCSCommand implements Comparable<WHCSCommand> {

    /*
     * Used to keep refIds in commands unique. Incremented every time one is used.
     */
    private static byte uniqueRefIdTracker = 0x01;

    private byte refId;
    private byte opCode;
    private byte controlTarget;
    private byte result;

    private WHCSCommand() {

    }

    public WHCSCommand(byte opCode, byte controlTarget) {
        this(GetUniqueRefId(), opCode, controlTarget);
    }

    private WHCSCommand(byte refId, byte opCode, byte controlTarget) {
        this();
        this.refId = refId;
        this.opCode = opCode;
        this.controlTarget = controlTarget;
    }

    private WHCSCommand(byte refId, byte opCode, byte controlTarget, byte result) {
        this(refId, opCode, controlTarget);
        this.result = result;
    }

    public static WHCSCommand parseCommand(Byte byteArr[]) {
        if(byteArr.length < 3) {
            throw new Error("A command must be at least 3 bytes. [refID|coommandType|controlTarget].");
        }
        if(byteArr.length < 4) {
            return new WHCSCommand(byteArr[0], byteArr[1], byteArr[2]);
        }
        else {
            return new WHCSCommand(byteArr[0], byteArr[1], byteArr[2], byteArr[3]);
        }
    }

    public static WHCSCommand parseCommand(ArrayList<Byte> byteList) {
        return parseCommand(byteList.toArray(new Byte[byteList.size()]));
    }

    private static void incrementRefIdTracker() {
        if(uniqueRefIdTracker == 0xEF) {
            uniqueRefIdTracker = 0x01;
        }
        else {
            uniqueRefIdTracker++;
        }
    }

    public static byte GetUniqueRefId() {
        byte ret = uniqueRefIdTracker;
        incrementRefIdTracker();
        return ret;
    }

    public byte getRefId() {
        return refId;
    }

    public static WHCSCommand CreateGetBaseStationStatusDEBUGCommand() {
        return new WHCSCommand((byte)0x00, WHCSOpCodes.GET_STATUS_OF_BASE_STATION, (byte)0x00);
    }

    public static WHCSCommand CreateQueryIfBaseStationCommand() {
        return new WHCSCommand(GetUniqueRefId(), WHCSOpCodes.QUERY_IF_BASE_STATION, (byte)0x04);
    }

    @Override
    public int compareTo(WHCSCommand another) {
        return another.refId - this.refId;
    }

    public byte[] toByteArray() {
        return new byte[] {refId, opCode, controlTarget};
    }
}
