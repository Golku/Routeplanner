package com.example.routeplanner.features.container.mapFragment;

import android.content.Context;
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
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Address userLocation;
    private Address currentAddress;
    private Address previousSelectedAddress;

    private List<Address> addressList;
    private List<Drive> driveList;
    private List<Address> routeOrder;

    MapController(MvcMap.View view, List<Address> addressList, List<Drive> driveList) {
        this.view = view;
        this.addressList = addressList;
        this.driveList = driveList;
        routeOrder = new ArrayList<>();

        userLocation = new Address();
        userLocation.setAddress("Vrij-Harnasch 21, Den Hoorn");
        userLocation.setLat(52.008234);
        userLocation.setLng(4.312999);
        userLocation.setUserLocation(true);

        previousSelectedAddress = userLocation;
    }

    @Override
    public void setMapData(GoogleMap googleMap, Context context) {

        clusterManager = new ClusterManager<>(context, googleMap);
        renderer = new CustomClusterRenderer(context, googleMap, clusterManager, routeOrder, driveList);
        clusterManager.setRenderer(renderer);

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
            return false;
        }

        if (address.isSelected()) {

            if (previousSelectedAddress.equals(address)) {

                address.setSelected(false);
                address.setCompleted(false);
                routeOrder.remove(address);

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
        marker.showInfoWindow();

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

        Log.d(debugTag, "Event received on mapFragment: " + event.getEventName());

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
        Log.d(debugTag, "updating markers");
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

    private void driveCompleted(Address address) {
        for (Address it: routeOrder) {
            if (it.getAddress().equals(address.getAddress())) {
                it.setCompleted(true);
                renderer.changeMarkerIcon(it);
                break;
            }
        }
    }
}