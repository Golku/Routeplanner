package com.example.routeplanner.data.pojos.api;

import java.util.List;

public class OrganizeRouteRequest {

    private String username;
    private List<String> routeList;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRouteList() {
        return routeList;
    }

    public void setRouteList(List<String> routeList) {
        this.routeList = routeList;
    }
}
