package com.whcs_ucf.whcs_android;

/**
 * Created by Jimmy on 6/8/2015.
 */
public class DebugFlags {
    //This flag is used when running the application on a virtual machine because the virtual machine will not have Bluetooth Capabilities
    //Turning off Bluetooth actions in the code for compatibility with vm can be done with this flag.
    public static final boolean RUNNING_ON_VM = true;

    //This flag is used to cause the listview in the boot activity to bring up a debug activity that is multipurpose.
    public static final boolean START_DEBUG_ACTIVITY_FROM_LIST_VIEW = true;

    //This flag is used in the communication pipeline to switch to a debug pipeline that tests
    // the functionality of pipeline given stubbed out data that mimics what would be received
    //from the WHCS Base Station.
    public static final boolean DEBUG_BLUETOOTH_COMM_PIPELINE = true;

    //
    public static final boolean DEBUG_CONTROL_MODULE_LIST_ACTIVITY_NO_BASESTATION_CONNECTION = true;
}
