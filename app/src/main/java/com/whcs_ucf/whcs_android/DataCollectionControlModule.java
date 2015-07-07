package com.whcs_ucf.whcs_android;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jimmy on 6/8/2015.
 */
public class DataCollectionControlModule extends ControlModule {
    private byte sensorValue = -1;

    public DataCollectionControlModule(ControlModuleRole role) {
        super(role);
    }

    public DataCollectionControlModule(ControlModuleRole role, byte identityNumber, DatabaseHandler databaseHandler) {
        super(role, identityNumber, databaseHandler);
    }

    public DataCollectionControlModule(Parcel in) {
        super(in);
        String[] data= new String[4];

        in.readStringArray(data);
        this.sensorValue = Byte.parseByte(data[3]);
    }

    public byte getSensorValue() {
        return sensorValue;
    }

    public void setSensorValue(byte b) {
        sensorValue = b;
    }

    @Override
    public String toString() {
        return super.toString() + ", Sensor Value: " + Byte.toString(this.getSensorValue());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest,flags);
        dest.writeStringArray(new String[]{Integer.toString(this.getIdentityNumber()), getRole().getRoleNameRepresentation(), getName(), Byte.toString(this.getSensorValue())});
    }

    public static final Parcelable.Creator<DataCollectionControlModule> CREATOR = new Parcelable.Creator<DataCollectionControlModule>() {

        @Override
        public DataCollectionControlModule createFromParcel(Parcel source) {
            return new DataCollectionControlModule(source);
        }

        @Override
        public DataCollectionControlModule[] newArray(int size) {
            return new DataCollectionControlModule[size];
        }
    };

    @Override
    public String statusableGetString() {
        return Byte.toString(this.getSensorValue());
    }
}
