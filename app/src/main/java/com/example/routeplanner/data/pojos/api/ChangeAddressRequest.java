package com.example.routeplanner.data.pojos.api;

public class ChangeAddressRequest {
    private String username;
    private String oldAddress;
    private String newAddress;

    public ChangeAddressRequest(String oldAddress, String newAddress) {
        this.oldAddress = oldAddress;
        this.newAddress = newAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOldAddress() {
        return oldAddress;
    }

    public void setOldAddress(String oldAddress) {
        this.oldAddress = oldAddress;
    }

    public String getNewAddress() {
        return newAddress;
    }

    public void setNewAddress(String newAddress) {
        this.newAddress = newAddress;
    }
}
