package com.example.routeplanner.features.container.mapFragment;

import android.content.Context;

import com.example.routeplanner.data.pojos.Event;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public interface MvcMap {

    interface View{

        void deselectedMultipleMarkers();

        void postEvent(Event event);

        void showToast(String message);
    }

    interface Controller{

        void setMapData(GoogleMap googleMap, Context context);

        void getUserLocation();

        void multipleMarkersDeselected();

        void eventReceived(Event event);
    }

}
