package com.example.routeplanner.data.pojos.api;

import java.util.List;

public class UpdateDriveListRequest {

    String username;
    List<Drive> driveList;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Drive> getDriveList() {
        return driveList;
    }

    public void setDriveList(List<Drive> driveList) {
        this.driveList = driveList;
    }
}
