package com.example.routeplanner.data.pojos;

import android.os.Parcel;
import android.os.Parcelable;

public class CommentInformation implements Parcelable {

    private String employeeId;
    private String date;
    private String comment;

    public CommentInformation(){

    }

    private CommentInformation(Parcel in) {
        employeeId = in.readString();
        date = in.readString();
        comment = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(employeeId);
        dest.writeString(date);
        dest.writeString(comment);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CommentInformation> CREATOR = new Creator<CommentInformation>() {
        @Override
        public CommentInformation createFromParcel(Parcel in) {
            return new CommentInformation(in);
        }

        @Override
        public CommentInformation[] newArray(int size) {
            return new CommentInformation[size];
        }
    };

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
