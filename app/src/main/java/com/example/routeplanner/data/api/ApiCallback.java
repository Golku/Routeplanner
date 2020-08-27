package com.example.routeplanner.data.api;

import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.api.Container;
import com.example.routeplanner.data.pojos.api.Drive;
import com.example.routeplanner.data.pojos.api.OrganizedRouteResponse;

public interface ApiCallback {

    interface ContainerResponseCallback {
        void containerResponse(Container response);
        void containerResponseFailure();
    }

    interface AddAddressCallback {
        void addressResponse(Address response);
        void addressResponseFailure();
    }

    interface OrganizeRouteCallback {
        void organizeRouteResponse(OrganizedRouteResponse response);
        void organizedRouteFailure();
    }

    interface DriveResponseCallback {
        void driveResponse(Drive response);
        void driveResponseFailure();

    }
}
