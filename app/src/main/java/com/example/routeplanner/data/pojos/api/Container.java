package com.example.routeplanner.data.pojos.api;

import com.example.routeplanner.data.pojos.Address;

import java.util.List;

public class Container {

    private String username;
    private String routeCode;
    private int routeState;
    private List<Address> addressList;
    private List<Drive> driveList;
    private int privateAddressCount;
    private int businessAddressCount;
    private int invalidAddressCount;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public int getRouteState() {
        return routeState;
    }

    public void setRouteState(int routeState) {
        this.routeState = routeState;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    public List<Drive> getDriveList() {
        return driveList;
    }

    public void setDriveList(List<Drive> driveList) {
        this.driveList = driveList;
    }

    public int getPrivateAddressCount() {
        return privateAddressCount;
    }

    public void setPrivateAddressCount(int privateAddressCount) {
        this.privateAddressCount = privateAddressCount;
    }

    public int getBusinessAddressCount() {
        return businessAddressCount;
    }

    public void setBusinessAddressCount(int businessAddressCount) {
        this.businessAddressCount = businessAddressCount;
    }

    public int getInvalidAddressCount() {
        return invalidAddressCount;
    }

    public void setInvalidAddressCount(int invalidAddressCount) {
        this.invalidAddressCount = invalidAddressCount;
    }
}
