package com.whcs_ucf.whcs_android;

/**
 * Created by Jimmy on 7/6/2015.
 */
public class RandomControlModuleGenerator {

    public static ControlModule GenerateRandomizedControlModule() {
        ControlModule cm = null;

        double randDouble = Math.random();

        if(randDouble < .5) {
            ControlModuleRole role = ControlModuleRole.GetRandomControlModuleRole();
            while(role == ControlModuleRole.SENSOR_COLLECTOR) {
                role = ControlModuleRole.GetRandomControlModuleRole();
            }

            ToggleableControlModule tcm = new ToggleableControlModule(role);

            if(Math.random() < .5) {
                tcm.setStatus(ToggleableControlModule.ToggleableState.ON);
            }
            cm = tcm;
        }
        else {
            DataCollectionControlModule dcm = new DataCollectionControlModule(ControlModuleRole.SENSOR_COLLECTOR);
            dcm.setSensorValue((byte)(int)(Math.random() * Byte.MAX_VALUE));
            cm = dcm;
        }

        cm.randomizeIdentityNumber();
        return cm;
    }

    public static ControlModule GenerateRandomizedControlModule(DatabaseHandler databaseHandler) {
        ControlModule cm = null;

        double randDouble = Math.random();

        if(randDouble < .5) {
            ControlModuleRole role = ControlModuleRole.GetRandomControlModuleRole();
            while(role == ControlModuleRole.SENSOR_COLLECTOR) {
                role = ControlModuleRole.GetRandomControlModuleRole();
            }

            ToggleableControlModule tcm = new ToggleableControlModule(role, (byte)(int)(Math.random() * Byte.MAX_VALUE), databaseHandler);

            if(Math.random() < .5) {
                tcm.setStatus(ToggleableControlModule.ToggleableState.ON);
            }
            cm = tcm;
        }
        else {
            DataCollectionControlModule dcm = new DataCollectionControlModule(ControlModuleRole.SENSOR_COLLECTOR, (byte)(int)(Math.random() * Byte.MAX_VALUE), databaseHandler);
            dcm.setSensorValue((byte)(int)(Math.random() * Byte.MAX_VALUE));
            cm = dcm;
        }

        return cm;
    }
}
