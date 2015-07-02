package com.whcs_ucf.whcs_android;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jimmy on 6/8/2015.
 */
public class ControlModule implements Parcelable{
    private int identityNumber;
    private ControlModuleRole role;
    private String name;

    public ControlModule(ControlModuleRole role) {
        this.role = role;
    }

    public ControlModule(Parcel in) {
        String[] data= new String[3];

        in.readStringArray(data);
        this.identityNumber = Integer.parseInt(data[0]);
        this.role = ControlModuleRole.parseRole(data[1]);
        this.name = data[2];
    }

    public ControlModuleRole getRole() {
        return role;
    }

    public String getName() {
        if(name == null) {
            return role.getRoleNameRepresentation();
        }
        return name;
    }

    public void setName(String n) {
        name = n;
    }

    public int getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(int n) {
        identityNumber = n;
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
}
