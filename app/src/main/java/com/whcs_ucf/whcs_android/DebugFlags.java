package com.whcs_ucf.whcs_android;

/**
 * Created by Jimmy on 6/8/2015.
 */
public class DebugFlags {
    //This flag is used when running the application on a virtual machine because the virtual machine will not have Bluetooth Capabilities
    //Turning off Bluetooth actions in the code for compatibility with vm can be done with this flag.
    public static final boolean RUNNING_ON_VM = false;

    //This flag is used to cause the listview in the boot activity to bring up a debug activity that is multipurpose.
    public static final boolean START_DEBUG_ACTIVITY_FROM_LIST_VIEW = true;

    //This flag is used in the communication pipeline to switch to a debug pipeline that tests
    // the functionality of pipeline given stubbed out data that mimics what would be received
    //from the WHCS Base Station.
    public static final boolean DEBUG_BLUETOOTH_COMM_PIPELINE = false;

    //Used in Base Station Connection Activity to bypass initializing the issuer and listener.
    //The issuer and listener cannot be initialized if there is no base station device to create a socket with.
    public static final boolean DEBUG_CONTROL_MODULE_LIST_ACTIVITY_NO_BASESTATION_CONNECTION = false;

    //prevents initializing the issuer and listener in the ControlModuleListActivity
    public static final boolean PREVENT_INITIALIZING_ISSUER_AND_LISTENER = false;

    //Performs necessary routing for just sending base station query from Android application.
    //Makes a branch in BaseStationConnectActivity whenever a bluetooth device is selected.
    //The bluetooth device is simply queried to check if it is the WHCS base station and then data is logged
    public static final boolean PERFORM_DEBUG_BASE_STATION_QUERY_FROM_BASE_STATION_CONNECT_ACTIVITY = false;
}
