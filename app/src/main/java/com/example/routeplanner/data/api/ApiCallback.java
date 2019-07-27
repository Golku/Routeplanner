package com.example.routeplanner.data.api;

import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.api.Container;
import com.example.routeplanner.data.pojos.api.Drive;

public interface ApiCallback {

    interface ContainerResponseCallback {
        void containerResponse(Container response);
        void containerResponseFailure();
    }

    interface AddAddressCallback {
        void addressResponse(Address response);
        void addressResponseFailure();
    }

    interface DriveResponseCallback {
        void driveResponse(Drive response);
        void driveResponseFailure();

    }
}
