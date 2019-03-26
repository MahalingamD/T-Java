package com.angler.ti_customer.commonmodel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kirandani on 19-05-2016.
 */
public class Version implements Parcelable {

    private String myCode = "";
    private String myMessage = "";
    private String myUpdateType = "";
    private String myUpdateURL = "";
    private String myCurrentVersion = "";
    private String myStatus = "";

    public String getMyStatus() {
        return myStatus;
    }

    public void setMyStatus(String myStatus) {
        this.myStatus = myStatus;
    }

    public String getMyCurrentVersion() {
        return myCurrentVersion;
    }

    public void setMyCurrentVersion(String myCurrentVersion) {
        this.myCurrentVersion = myCurrentVersion;
    }


    public String getMyUpdateURL() {
        return myUpdateURL;
    }

    public void setMyUpdateURL(String myUpdateURL) {
        this.myUpdateURL = myUpdateURL;
    }


    public String getMyCode() {
        return myCode;
    }

    public void setMyCode(String myCode) {
        this.myCode = myCode;
    }

    public String getMyMessage() {
        return myMessage;
    }

    public void setMyMessage(String myMessage) {
        this.myMessage = myMessage;
    }

    public String getMyUpdateType() {
        return myUpdateType;
    }

    public void setMyUpdateType(String myUpdateType) {
        this.myUpdateType = myUpdateType;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.myCode);
        dest.writeString(this.myMessage);
        dest.writeString(this.myUpdateType);
    }

    public Version() {
    }

    protected Version(Parcel in) {
        this.myCode = in.readString();
        this.myMessage = in.readString();
        this.myUpdateType = in.readString();
    }

    public static final Creator<Version> CREATOR = new Creator<Version>() {
        @Override
        public Version createFromParcel(Parcel source) {
            return new Version(source);
        }

        @Override
        public Version[] newArray(int size) {
            return new Version[size];
        }
    };
}
