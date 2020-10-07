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
import com.google.maps.android.clustering.ClusterManager;
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

    private Address userLocation;
    private Address currentAddress;
    private Address previousSelectedAddress;

    private List<Address> addressList;
    private List<Drive> driveList;
    private List<Address> routeOrder;

    private int index;
    private boolean firstIteration;

    private HashMap<String, Polyline> polylineHashMap;

    MapController(MvcMap.View view, List<Address> addressList, List<Drive> driveList) {
        this.view = view;
        this.addressList = addressList;
        this.driveList = driveList;
        this.routeOrder = new ArrayList<>();
        this.index = 0;
        this.firstIteration = true;

        userLocation = new Address();
        userLocation.setAddress("Vrij-Harnasch 21, Den Hoorn");
        userLocation.setLat(52.008234);
        userLocation.setLng(4.312999);
        userLocation.setUserLocation(true);

        previousSelectedAddress = userLocation;

        createEvent("container", "setupRouteOrder", routeOrder, this);
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
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        clusterManager.addItem(userLocation);

        for (Address address : addressList) {
            if (address.isValid()) {
                clusterManager.addItem(address);
            }
        }

        clusterManager.cluster();

        this.googleMap = googleMap;

        CameraPosition cameraPosition = CameraPosition.builder().target(new LatLng(userLocation.getLat(), userLocation.getLng())).zoom(12f).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        getUserLocation();
    }

    private void moveMapCamera(double lat, double lng) {
        float zoom = googleMap.getCameraPosition().zoom;
        CameraPosition cameraPosition = CameraPosition.builder().target(new LatLng(lat, lng)).zoom(zoom).build();
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
//            Log.d(debugTag, "Showing infoWindow");
//            return false;
        }else{
            Log.d(debugTag, "Proceeding");
        }

        if (address.isSelected()) {

            if (previousSelectedAddress.equals(address)) {

                address.setSelected(false);
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

    @Override
    public void optimiseRoute() {
        createEvent("container", "optimiseRoute", this);
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
            case "organizingRoute":
                organizingRoute(event.isOrganizingRoute());
                break;
            case "driveSuccess":
                driveSuccess();
                break;
            case "driveFailed":
                driveFailed();
                break;
            case "driveCompleted":
                driveCompleted(event.getAddressString());
                break;
            case "updateRouteOrder":
                updateRouteOrder(event.getRouteInfo().getDriveList());
                break;
        }
    }

    private void updateMarkers() {
        for(Address address : addressList){
            renderer.changeMarkerIcon(address);
        }
    }

    private void updateRouteOrder(List<Drive> driveList){

        index = 0;

        if(!routeOrder.isEmpty()){
            previousSelectedAddress = findAddress(driveList.get(0).getDestinationAddress());
            index = routeOrder.indexOf(previousSelectedAddress)+1;
        }

        currentAddress = findAddress(driveList.get(0).getDestinationAddress());
//        Log.d(debugTag, "currentAddress first: " + currentAddress.getAddress());

        for(Drive drive : driveList){
            for(Address address : addressList){
                if(address.getAddress().equals(drive.getDestinationAddress())){
                    address.setSelected(true);
                    routeOrder.add(address);
                }
            }
        }
        addingMultiplePolyline(driveList);
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
            if(firstIteration){
                if(!driveList.isEmpty()){
                    updateRouteOrder(driveList);
                }
                firstIteration = false;
            }
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

    private void organizingRoute(boolean organizingRoute) {
        if(organizingRoute){
            googleMap.setOnMarkerClickListener(null);
        }else{
            googleMap.setOnMarkerClickListener(this);
        }
        view.disableMapBtn(organizingRoute);
    }

    private void driveSuccess() {
        currentAddress.setFetchingDriveInfo(false);
        currentAddress.setSelected(true);
        routeOrder.add(currentAddress);
        index = routeOrder.size();
        findPolyline();
        previousSelectedAddress = currentAddress;
        renderer.changeMarkerIcon(currentAddress);
        googleMap.setOnMarkerClickListener(this);
    }

    private void driveFailed() {
        currentAddress.setFetchingDriveInfo(false);
        renderer.changeMarkerIcon(currentAddress);
        googleMap.setOnMarkerClickListener(this);
//        view.showToast("Failed to get drive");
        view.showDialog("Failed to get drive");
    }

    private void addingMultiplePolyline(List<Drive> driveList){
        for(Drive drive: driveList){
            view.addPolylineToMap(drive.getPolyline(), googleMap);
        }
    }

    private void findPolyline(){
        for(Drive drive: driveList){
            if(drive.getDestinationAddress().contains(currentAddress.getAddress())){
                if(drive.getDestinationAddress().contains(currentAddress.getCity())){
                    view.addPolylineToMap(drive.getPolyline(), googleMap);
                }
            }
        }
    }

    @Override
    public void storePolyline(Polyline polyline) {

        Address address = currentAddress;
        polylineHashMap.put(address.getAddress(), polyline);
        Log.d(debugTag, "storePolyline address: " + address.getAddress());
        if(routeOrder.indexOf(address) == 0){
            Log.d(debugTag, "First in routOrder: " + address.getAddress());
            view.updatePolyline(polylineHashMap.get(address.getAddress()));
        }

        renderer.changeMarkerIcon(currentAddress);

        Log.d(debugTag, "From: " + previousSelectedAddress.getAddress());
        Log.d(debugTag, "To: " + currentAddress.getAddress());
        Log.d(debugTag, " ");
        previousSelectedAddress = currentAddress;
        if(index < routeOrder.size()-1){
            previousSelectedAddress = findAddress(routeOrder.get(index).getAddress());
            index++;
            currentAddress = findAddress(routeOrder.get(index).getAddress());
            Log.d(debugTag, "finding polyline");
            findPolyline();
        }
    }

    private void removePolyline(String address){
        Log.d(debugTag, "removePolyline address: " + address);
        if(polylineHashMap.get(address) != null){
            Log.d(debugTag, "Removing: " + address);
            Log.d(debugTag, " ");
            view.removePolylineFromMap(polylineHashMap.get(address));
            polylineHashMap.remove(address);
        }else{
            Log.d(debugTag, "Address is null");
        }
    }

    private void driveCompleted(String address) {
        for (Address it: routeOrder) {
            if (it.getAddress().equals(address)) {
                it.setCompleted(true);
                removePolyline(address);
                view.updatePolyline(polylineHashMap.get(routeOrder.get(routeOrder.indexOf(it)+1).getAddress()));
                renderer.changeMarkerIcon(it);
                break;
            }
        }
    }
}