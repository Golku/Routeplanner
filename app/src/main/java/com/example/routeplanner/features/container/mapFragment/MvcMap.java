package com.example.routeplanner.features.container.mapFragment;

import android.content.Context;

import com.example.routeplanner.data.pojos.Event;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.maps.model.DirectionsResult;

import java.util.List;

public interface MvcMap {

    interface View{

        void deselectedMultipleMarkers();

        void addPolylineToMap(List<LatLng> newDecodedPath, GoogleMap googleMap);

        void removePolylineFromMap(Polyline polyline);

        void updatePolyline(Polyline polyline);

        void postEvent(Event event);

        void showToast(String message);
    }

    interface Controller{

        void setMapData(GoogleMap googleMap, MapFragment fragment);

        void storePolyline(Polyline polyline);

        void getUserLocation();

        void multipleMarkersDeselected();

        void eventReceived(Event event);
    }

}
