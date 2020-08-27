package com.example.routeplanner.data.pojos.api;

import com.example.routeplanner.data.pojos.Address;

import java.util.List;

public class UpdatePackageCountRequest {

    String username;
    private List<String> addressList;
    private List<Integer> countList;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<String> addressList) {
        this.addressList = addressList;
    }

    public List<Integer> getCountList() {
        return countList;
    }

    public void setCountList(List<Integer> countList) {
        this.countList = countList;
    }
}
