package com.example.routeplanner.data.pojos;

import com.example.routeplanner.data.pojos.api.ChangeAddressRequest;
import com.example.routeplanner.data.pojos.api.Drive;
import com.example.routeplanner.data.pojos.api.DriveRequest;

import java.util.List;

public class Event {

    private String receiver;
    private String eventName;
    private String addressString;
    private int position;
    private Address address;
    private Drive drive;
    private List<Address> addressList;
    private List<Drive> driveList;
    private DriveRequest driveRequest;
    private ChangeAddressRequest changeAddressRequest;

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getAddressString() {
        return addressString;
    }

    public void setAddressString(String addressString) {
        this.addressString = addressString;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Drive getDrive() {
        return drive;
    }

    public void setDrive(Drive drive) {
        this.drive = drive;
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

    public DriveRequest getDriveRequest() {
        return driveRequest;
    }

    public void setDriveRequest(DriveRequest driveRequest) {
        this.driveRequest = driveRequest;
    }

    public ChangeAddressRequest getChangeAddressRequest() {
        return changeAddressRequest;
    }

    public void setChangeAddressRequest(ChangeAddressRequest changeAddressRequest) {
        this.changeAddressRequest = changeAddressRequest;
    }
}
