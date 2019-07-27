package com.example.routeplanner.data.pojos.api;

import com.example.routeplanner.data.pojos.Address;

public class Drive{

    private boolean valid;

    private int position;

    private Address originAddress;
    private Address destinationAddress;

    private boolean destinationIsABusiness;

    private long driveDurationInSeconds;
    private String driveDurationHumanReadable;

    private long driveDistanceInMeters;
    private String driveDistanceHumanReadable;

    private long deliveryTimeInMillis;
    private String deliveryTimeHumanReadable;

    private int done;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Address getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(Address originAddress) {
        this.originAddress = originAddress;
    }

    public Address getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(Address destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public boolean isDestinationIsABusiness() {
        return destinationIsABusiness;
    }

    public void setDestinationIsABusiness(boolean destinationIsABusiness) {
        this.destinationIsABusiness = destinationIsABusiness;
    }

    public long getDriveDurationInSeconds() {
        return driveDurationInSeconds;
    }

    public void setDriveDurationInSeconds(long driveDurationInSeconds) {
        this.driveDurationInSeconds = driveDurationInSeconds;
    }

    public String getDriveDurationHumanReadable() {
        return driveDurationHumanReadable;
    }

    public void setDriveDurationHumanReadable(String driveDurationHumanReadable) {
        this.driveDurationHumanReadable = driveDurationHumanReadable;
    }

    public long getDriveDistanceInMeters() {
        return driveDistanceInMeters;
    }

    public void setDriveDistanceInMeters(long driveDistanceInMeters) {
        this.driveDistanceInMeters = driveDistanceInMeters;
    }

    public String getDriveDistanceHumanReadable() {
        return driveDistanceHumanReadable;
    }

    public void setDriveDistanceHumanReadable(String driveDistanceHumanReadable) {
        this.driveDistanceHumanReadable = driveDistanceHumanReadable;
    }

    public long getDeliveryTimeInMillis() {
        return deliveryTimeInMillis;
    }

    public void setDeliveryTimeInMillis(long deliveryTimeInMillis) {
        this.deliveryTimeInMillis = deliveryTimeInMillis;
    }

    public String getDeliveryTimeHumanReadable() {
        return deliveryTimeHumanReadable;
    }

    public void setDeliveryTimeHumanReadable(String deliveryTimeHumanReadable) {
        this.deliveryTimeHumanReadable = deliveryTimeHumanReadable;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }
}
