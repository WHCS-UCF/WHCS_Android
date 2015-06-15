package com.whcs_ucf.whcs_android;

/**
 * Created by Jimmy on 6/15/2015.
 */
public class WHCSOpCodes {
    /*
     * The OpCodes for WHCS are directly related to the packet structure that WHCS parsers will be
     * expecting.
     * An opcode for a name change for a module will notify that there will be a field that has the
     * length of the name of the affected control module.
     */

    /*
     * commands for WHCS
     */
    public static final byte GET_STATUS_OF_BASE_STATION = 0x00;
    public static final byte GET_MODULE_STATUS = 0X01;
    public static final byte TURN_ON_MODULE = 0x02;
    public static final byte TURN_OFF_MODULE = 0x03;
    public static final byte TOGGLE_MODULE = 0x04;
    public static final byte GET_CONTROL_MODULE_TYPE = 0x05;
    public static final byte GET_DATA_COLLECTOR_DATA = 0x06;
    public static final byte GET_NUMBER_OF_MODULES = 0x07;
    public static final byte GET_CONTROL_MODULE_UID = 0x08;
    public static final byte SET_UPDATE_INTERVAL = 0x09;

    /*
     * responses for WHCS
     */
    public static final byte SUCCESS_NO_RESULT = 0x50;
    public static final byte SUCCESS_WITH_RESULT = 0x51;
    public static final byte ERROR_NO_RESULT = 0x52;
    public static final byte ERROR_WITH_RESULT = 0x53;

    /*
     * base station push op codes
     */
    public static final byte CONTROL_MODULES_CHANGED = 0x70;

    public static WHCSResponse.ResponseType getWHCSResponseTypeFromOpCode(byte opCode) {
        return WHCSResponse.ResponseType.NO_RESULT;
    }
}
