package com.whcs_ucf.whcs_android;

/**
 * Created by Jimmy on 6/8/2015.
 */
public enum ControlModuleRole {
    OUTLET_CONTROLLER,
    LIGHT_CONTROLLER,
    DOOR_CONTROLLER,
    SENSOR_COLLECTOR;

    public String getRoleNameRepresentation() {
        switch(this) {
            case OUTLET_CONTROLLER:
                return "Outlet";
            case LIGHT_CONTROLLER:
                return "Light";
            case DOOR_CONTROLLER:
                return "Door";
            case SENSOR_COLLECTOR:
                return "Sensor";
            default:
                throw new Error("Control Module Role is not mapped to a name string.");
        }
    }

    public static ControlModuleRole GetRandomControlModuleRole() {
        int roleNum = (int)(Math.random() * 4);
        switch(roleNum) {
            case 0:
                return OUTLET_CONTROLLER;
            case 1:
                return LIGHT_CONTROLLER;
            case 2:
                return DOOR_CONTROLLER;
            case 3:
                return SENSOR_COLLECTOR;
            default:
                return OUTLET_CONTROLLER;
        }
    }
}
