package com.whcs_ucf.whcs_android;

/**
 * Created by Jimmy on 7/7/2015.
 */
public class Utils {

    public static String HexStringFromByteArray(byte[] arr) {
        String retString = "";
        for(int i = 0;i < arr.length; i++) {
            retString += (String.format("%02x ", arr[i] & 0xff));
        }
        return  retString;
    }

    public static String HexStringFromByteArray(byte[] arr, int length) {
        String retString = "";
        for (int i = 0; i < length; i++) {
            retString += (String.format("%02x ", arr[i] & 0xff));
        }
        return retString;
    }
}
