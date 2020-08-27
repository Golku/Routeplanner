package com.example.routeplanner.data.pojos;

import com.example.routeplanner.data.pojos.api.Drive;

import java.util.List;

public class RouteInfo {

    private List<Drive> driveList;

    public List<Drive> getDriveList() {
        return driveList;
    }

    public void setDriveList(List<Drive> driveList) {
        this.driveList = driveList;
    }
}
