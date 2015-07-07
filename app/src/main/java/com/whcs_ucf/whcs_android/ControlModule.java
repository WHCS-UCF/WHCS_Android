package com.whcs_ucf.whcs_android;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jimmy on 6/8/2015.
 */
public class ControlModule implements Parcelable, Statusable{
    private byte identityNumber;
    private ControlModuleRole role;
    private String name;

    public ControlModule(ControlModuleRole role) {
        this.role = role;
    }

    public ControlModule(ControlModuleRole role, byte identityNumber, DatabaseHandler databaseHandler) {
        this(role);
        this.setIdentityNumber(identityNumber);

        refreshName(databaseHandler);
    }

    public ControlModule(Parcel in) {
        String[] data= new String[3];

        in.readStringArray(data);
        this.identityNumber = Byte.parseByte(data[0]);
        this.role = ControlModuleRole.parseRole(data[1]);
        this.name = data[2];
    }

    public ControlModuleRole getRole() {
        return role;
    }

    public String getName() {
        if(name == null) {
            return role.getRoleNameRepresentation() + " " + Byte.toString(this.getIdentityNumber());
        }
        return name;
    }

    public void setName(String n) {
        name = n;
    }

    public byte getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(byte n) {
        identityNumber = n;
    }

    public void randomizeIdentityNumber() {
        this.identityNumber = (byte)(int)(Math.random() * Byte.MAX_VALUE);
    }

    public void refreshName(DatabaseHandler databaseHandler) {
        ControlModule checkIfExistCM = databaseHandler.getControlModule(identityNumber);
        if(checkIfExistCM != null) {
            this.setName(checkIfExistCM.getName());
        }
    }

    @Override
    public String toString() {
        return "Identity #: " + Integer.toString(this.getIdentityNumber()) +", Control Module Role: " +getRole().getRoleNameRepresentation() +", Name: " + getName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{Integer.toString(this.getIdentityNumber()), getRole().getRoleNameRepresentation(), getName()});
    }

    public static final Parcelable.Creator<ControlModule> CREATOR = new Parcelable.Creator<ControlModule>() {

        @Override
        public ControlModule createFromParcel(Parcel source) {
            return new ControlModule(source);
        }

        @Override
        public ControlModule[] newArray(int size) {
            return new ControlModule[size];
        }
    };

    @Override
    public String statusableGetString() {
        return "";
    }
}
