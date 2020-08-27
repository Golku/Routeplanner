package com.example.routeplanner.data.pojos;

import android.os.Parcel;
import android.os.Parcelable;

public class DialogMessage implements Parcelable {

    String message;

    public DialogMessage(String message) {
        this.message = message;
    }

    protected DialogMessage(Parcel in) {
        message = in.readString();
    }

    public static final Creator<DialogMessage> CREATOR = new Creator<DialogMessage>() {
        @Override
        public DialogMessage createFromParcel(Parcel in) {
            return new DialogMessage(in);
        }

        @Override
        public DialogMessage[] newArray(int size) {
            return new DialogMessage[size];
        }
    };

    public String getMessage() {
        return message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
    }
}
