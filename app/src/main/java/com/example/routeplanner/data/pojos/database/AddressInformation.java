package com.example.routeplanner.data.pojos.database;

import java.util.ArrayList;

public class AddressInformation {

    private int commentsCount;

    private ArrayList<String> employeeId;
    private ArrayList<String> comments;
    private ArrayList<String> dates;

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public ArrayList<String> getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(ArrayList<String> employeeId) {
        this.employeeId = employeeId;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }

    public ArrayList<String> getDates() {
        return dates;
    }

    public void setDates(ArrayList<String> dates) {
        this.dates = dates;
    }

}
