package com.example.routeplanner.features.container.mapFragment;

import android.util.Log;

import com.example.routeplanner.data.models.CustomClusterRenderer;
import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.Event;
import com.example.routeplanner.data.pojos.api.Drive;
import com.example.routeplanner.data.pojos.api.DriveRequest;
import com.example.routeplanner.features.shared.BaseController;
import com.example.routeplanner.features.shared.MvcBaseController;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapController extends BaseController implements
        MvcBaseController,
        MvcMap.Controller,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {

    private final String debugTag = "debugTag";

    private MvcMap.View view;

    private GoogleMap googleMap;
    private ClusterManager<Address> clusterManager;
    private CustomClusterRenderer renderer;
    private GeoApiContext geoApiContext;

    private Address userLocation;
    private Address currentAddress;
    private Address previousSelectedAddress;

    private List<Address> addressList;
    private List<Drive> driveList;
    private List<Address> routeOrder;

    private HashMap<String, Polyline> polylineHashMap;

    MapController(MvcMap.View view, List<Address> addressList, List<Drive> driveList) {
        this.view = view;
        this.addressList = addressList;
        this.driveList = driveList;
        this.routeOrder = new ArrayList<>();
        this.geoApiContext = null;

        userLocation = new Address();
        userLocation.setAddress("Vrij-Harnasch 21, Den Hoorn");
        userLocation.setLat(52.008234);
        userLocation.setLng(4.312999);
        userLocation.setUserLocation(true);

        previousSelectedAddress = userLocation;
    }

    @Override
    public void setMapData(GoogleMap googleMap, MapFragment fragment) {

        clusterManager = new ClusterManager<>(fragment.getContext(), googleMap);
        renderer = new CustomClusterRenderer(fragment.getContext(), googleMap, clusterManager, routeOrder, driveList);
        clusterManager.setRenderer(renderer);
        polylineHashMap = new HashMap<>();

        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);

        clusterManager.addItem(userLocation);

        for (Address address : addressList) {
            if (address.isValid()) {
                clusterManager.addItem(address);
            }
        }

        clusterManager.cluster();

        this.googleMap = googleMap;

        if(geoApiContext == null){
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey("AIzaSyAycv4bRa_NI4gl7WwkgLGs4EDhn44G8DY")
                    .build();
        }

        moveMapCamera(userLocation.getLat(), userLocation.getLng());
    }

    private void moveMapCamera(double lat, double lng) {
        CameraPosition cameraPosition = CameraPosition.builder().target(new LatLng(lat, lng)).zoom(12f).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        createEvent("container", "itemClick", findAddress(marker.getTitle()), this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Address address = findAddress(marker.getTitle());

        if (address == null) {
            Log.d(debugTag, "MapController/onMarkerClick - Address is null");
            return false;
        }

        marker.showInfoWindow();

        marker.setTag(new Address());


        if(marker.isInfoWindowShown()){
            Log.d(debugTag, "Showing infoWindow");
//            return false;
        }else{
            Log.d(debugTag, "Proceeding");
        }

        if (address.isSelected()) {

            if (previousSelectedAddress.equals(address)) {

                address.setSelected(false);
                address.setCompleted(false);
                routeOrder.remove(address);

                removePolyline(address.getAddress());

                if (routeOrder.size() > 0) {
                    previousSelectedAddress = routeOrder.get(routeOrder.size() - 1);
                } else {
                    previousSelectedAddress = userLocation;
                }

                removeDrive();
            } else {
                currentAddress = address;
                view.deselectedMultipleMarkers();
            }
        } else {

            googleMap.setOnMarkerClickListener(null);
            currentAddress = address;
            address.setFetchingDriveInfo(true);
            getDrive(address);
        }

        renderer.changeMarkerIcon(address);

        return true;
    }

    private void getDrive(Address address) {

        DriveRequest request = new DriveRequest();

        request.setOrigin(previousSelectedAddress.getAddress());

        request.setDestination(address.getAddress());

        createEvent("container", "getDrive", request, this);
    }

    private void removeDrive() {
        createEvent("driveFragment", "removeDrive", this);
    }

    @Override
    public void multipleMarkersDeselected()  {

        int addressPosition = routeOrder.indexOf(currentAddress);

        for (int i = addressPosition; i < routeOrder.size(); i++) {
            Address address = routeOrder.get(i);
            address.setSelected(false);
            address.setCompleted(false);
            removePolyline(address.getAddress());
            renderer.changeMarkerIcon(address);
        }

        routeOrder.subList(addressPosition, routeOrder.size()).clear();

        if (routeOrder.size() > 0) {
            previousSelectedAddress = routeOrder.get(routeOrder.size() - 1);
        } else {
            previousSelectedAddress = userLocation;
        }

        createEvent("driveFragment", "RemoveMultipleDrive", currentAddress.getAddress(), this);
    }

    private Address findAddress(String addressString){
        Address address = null;
        for(Address it : addressList){
            if(it.getAddress().equals(addressString)){
                address = it;
                break;
            }
        }
        return address;
    }

    @Override
    public void publishEvent(Event event) {
        view.postEvent(event);
    }

    @Override
    public void eventReceived(Event event) {

        if (!(event.getReceiver().equals("mapFragment") || event.getReceiver().equals("all"))) {
            return;
        }

        //Log.d(debugTag, "Event received on mapFragment: " + event.getEventName());

        switch (event.getEventName()) {
            case "updateMarkers":
                updateMarkers();
                break;
            case "addressTypeChange":
                addressTypeChange(event.getAddress());
                break;
            case "showMarker":
                showMarker(event.getAddress());
                break;
            case "markAddress":
                addMarkerToMap(event.getAddress());
                break;
            case "removeMarker":
                removeMarkerFromMap(event.getAddress());
                break;
            case "driveSuccess":
                driveSuccess();
                break;
            case "driveFailed":
                driveFailed();
                break;
            case "driveCompleted":
                driveCompleted(event.getAddress());
                break;
        }
    }

    private void updateMarkers() {
//        Log.d(debugTag, "updating markers");
        for(Address address : addressList){
            renderer.changeMarkerIcon(address);
        }
    }

    @Override
    public void getUserLocation() {
        createEvent("container", "getUserLocation", this);
    }

    private void addressTypeChange(Address address) {
        renderer.changeMarkerIcon(findAddress(address.getAddress()));
    }

    private void showMarker(Address address) {
        if (address.isValid()) {
            Marker marker = renderer.getMarker(findAddress(address.getAddress()));
            moveMapCamera(address.getLat(), address.getLng());
            marker.showInfoWindow();
        }
    }

    private void addMarkerToMap(Address address) {
        if (address.isValid()) {
            if (address.isUserLocation()) {
                clusterManager.removeItem(userLocation);
                userLocation = address;
                if (routeOrder.size() == 0) {
                    previousSelectedAddress = address;
                }
            }
            clusterManager.addItem(address);
            clusterManager.cluster();
            moveMapCamera(address.getLat(), address.getLng());
        }
    }

    private void removeMarkerFromMap(Address address) {
        clusterManager.removeItem(address);
        clusterManager.cluster();

        if (address.isSelected()) {
            currentAddress = address;
            multipleMarkersDeselected();
        }
    }

    private void driveSuccess() {
        currentAddress.setFetchingDriveInfo(false);
        currentAddress.setSelected(true);
        routeOrder.add(currentAddress);
        calculateDirections();
        previousSelectedAddress = currentAddress;
        renderer.changeMarkerIcon(currentAddress);
        googleMap.setOnMarkerClickListener(this);
    }

    private void driveFailed() {
        currentAddress.setFetchingDriveInfo(false);
        renderer.changeMarkerIcon(currentAddress);
        googleMap.setOnMarkerClickListener(this);
        view.showToast("Failed to get drive");
    }

    private void calculateDirections(){
        com.google.maps.model.LatLng origin = new com.google.maps.model.LatLng(
                previousSelectedAddress.getLat(),
                previousSelectedAddress.getLng()
        );

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                currentAddress.getLat(),
                currentAddress.getLng()
        );

        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);
        directions.origin(origin);
        directions.destination(destination);
        directions.alternatives(false);

        directions.setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
//                Log.d(debugTag, "calculateDirections: routes: " + result.routes[0].toString());
//                Log.d(debugTag, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
//                Log.d(debugTag, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
//                Log.d(debugTag, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                for (DirectionsRoute route : result.routes) {
//                    Log.d(debugTag, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for (com.google.maps.model.LatLng latLng : decodedPath) {

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }

                    view.addPolylineToMap(newDecodedPath, googleMap);
                }
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(debugTag, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }

    @Override
    public void storePolyline(Polyline polyline) {
        polylineHashMap.put(currentAddress.getAddress(), polyline);
        if(routeOrder.indexOf(currentAddress) == 0){
            view.updatePolyline(polylineHashMap.get(currentAddress.getAddress()));
        }
    }

    private void removePolyline(String address){
        if(polylineHashMap.get(address) != null){
            view.removePolylineFromMap(polylineHashMap.get(address));
            polylineHashMap.remove(address);
        }
    }

    private void driveCompleted(Address address) {
        for (Address it: routeOrder) {
            if (it.getAddress().equals(address.getAddress())) {
                it.setCompleted(true);
                removePolyline(address.getAddress());
                view.updatePolyline(polylineHashMap.get(routeOrder.get(routeOrder.indexOf(it)+1).getAddress()));
                renderer.changeMarkerIcon(it);
                break;
            }
        }
    }
}