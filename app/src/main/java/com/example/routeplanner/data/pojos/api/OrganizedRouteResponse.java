package com.example.routeplanner.data.pojos.api;

import java.util.List;

public class OrganizedRouteResponse {

    List<Drive> organizedRoute;

    public List<Drive> getOrganizedRoute() {
        return organizedRoute;
    }

    public void setOrganizedRoute(List<Drive> organizedRoute) {
        this.organizedRoute = organizedRoute;
    }
}
