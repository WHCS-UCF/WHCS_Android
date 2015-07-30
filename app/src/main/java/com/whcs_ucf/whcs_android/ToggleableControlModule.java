package com.whcs_ucf.whcs_android;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jimmy on 6/8/2015.
 */
public class ToggleableControlModule extends ControlModule{
    private ToggleableState status;

    public ToggleableControlModule(ControlModuleRole role) {
        super(role);
    }

    public ToggleableControlModule(ControlModuleRole role, byte identityNumber, DatabaseHandler databaseHandler) {
        super(role, identityNumber, databaseHandler);
    }

    public ToggleableControlModule(Parcel in) {
        super(in);
        String[] data= new String[4];

        in.readStringArray(data);
        this.status = ToggleableState.valueOf(data[3]);
    }

    public ToggleableState toggle() {
        if(status == ToggleableState.OFF) {
            status = ToggleableState.ON;
        }
        else {
            status = ToggleableState.OFF;
        }
        return status;
    }

    public ToggleableState getStatus() {
        if(status == null) {
            return ToggleableState.OFF;
        }
        return status;
    }

    public void setStatus(ToggleableState state) {
        this.status = state;
    }

    public enum ToggleableState {
        OFF,
        ON
    }

    @Override
    public String toString() {
        return super.toString() + ", status: " +this.getStatus().name();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest,flags);
        dest.writeStringArray(new String[]{Integer.toString(this.getIdentityNumber()), getRole().getRoleNameRepresentation(), getName(), this.getStatus().name()});
    }

    public static final Parcelable.Creator<ToggleableControlModule> CREATOR = new Parcelable.Creator<ToggleableControlModule>() {

        @Override
        public ToggleableControlModule createFromParcel(Parcel source) {
            return new ToggleableControlModule(source);
        }

        @Override
        public ToggleableControlModule[] newArray(int size) {
            return new ToggleableControlModule[size];
        }
    };

    @Override
    public String statusableGetString() {
        return this.getStatus().name();
    }

    @Override
    public void updateStatus(byte statusRepresentation) {
        if(statusRepresentation == 0x00) {
            this.setStatus(ToggleableState.OFF);
        }
        else {
            this.setStatus(ToggleableState.ON);
        }
    }
}
