package com.whcs_ucf.whcs_android;

/**
 * Created by Jimmy on 6/15/2015.
 */
public class ExamplePackets {

    public static final byte[] NumberOfControlModuleCommand = new byte[] {0x00, WHCSOpCodes.GET_NUMBER_OF_MODULES, 0x00}; //refId : 0, opCode: GET_NUMBER_OF_MODULES, target: 0
    public static final byte[] NumberOfControlModuleResponse = new byte[] {0x00, WHCSOpCodes.SUCCESS_WITH_RESULT, 0x00, 0x04}; //refId : 0, opCode: don't care, target: don't care, resultByte: 4

    public static final byte[] ControlModuleTypeCommand = new byte[] {0x01, WHCSOpCodes.GET_CONTROL_MODULE_TYPE, 0x01};
    public static final byte[] ControlModuleTypeResponse = new byte[] {0x01, WHCSOpCodes.GET_CONTROL_MODULE_TYPE, 0x01, 0x02};
}
