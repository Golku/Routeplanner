package com.example.routeplanner.data.pojos.database;

public class AddressInformationResponse {

    private boolean error;
    private String message;
    private boolean informationAvailable;
    private AddressInformation addressInformation;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isInformationAvailable() {
        return informationAvailable;
    }

    public void setInformationAvailable(boolean informationAvailable) {
        this.informationAvailable = informationAvailable;
    }

    public AddressInformation getAddressInformation() {
        return addressInformation;
    }

    public void setAddressInformation(AddressInformation addressInformation) {
        this.addressInformation = addressInformation;
    }
}
