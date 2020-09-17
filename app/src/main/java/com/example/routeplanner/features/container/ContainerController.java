package com.example.routeplanner.features.container;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import com.example.routeplanner.data.api.ApiCallback;
import com.example.routeplanner.data.models.LocationManager;
import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.Event;
import com.example.routeplanner.data.pojos.MyApplication;
import com.example.routeplanner.data.pojos.RouteInfo;
import com.example.routeplanner.data.pojos.RouteInfoHolder;
import com.example.routeplanner.data.pojos.Session;
import com.example.routeplanner.data.pojos.api.AddressRequest;
import com.example.routeplanner.data.pojos.api.Container;
import com.example.routeplanner.data.pojos.api.Drive;
import com.example.routeplanner.data.pojos.api.DriveRequest;
import com.example.routeplanner.data.pojos.api.OrganizeRouteRequest;
import com.example.routeplanner.data.pojos.api.OrganizedRouteResponse;
import com.example.routeplanner.data.pojos.api.RemoveAddressRequest;
import com.example.routeplanner.data.pojos.api.UpdateDriveListRequest;
import com.example.routeplanner.features.shared.BaseController;
import com.example.routeplanner.features.shared.MvcBaseController;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ContainerController extends BaseController implements
        MvcBaseController,
        MvcContainer.Controller,
        ApiCallback.ContainerResponseCallback,
        ApiCallback.AddAddressCallback,
        ApiCallback.OrganizeRouteCallback,
        ApiCallback.DriveResponseCallback {

    private final String debugTag = "debugTag";

    private MvcContainer.View view;
    private ContainerActivity activity;
    private ContainerModel model;

    private Handler handler;
    private Context context;
    private Session session;
    private Container container;
    private PlacesClient placesClient;
    private SimpleDateFormat minutesFormat;
    private Address userLocation;
    private List<Address> routeOrder;
    private boolean updatingApiDriveList;
    private boolean organizingRoute;

    ContainerController(MvcContainer.View view, ContainerActivity activity, Session session) {
        this.view = view;
        this.activity = activity;
        this.handler = new Handler();
        this.session = session;
        this.context = activity;
        this.minutesFormat = new SimpleDateFormat("mm:ss");


        if (!Places.isInitialized()) {
            Places.initialize(context.getApplicationContext(), "AIzaSyAycv4bRa_NI4gl7WwkgLGs4EDhn44G8DY");
        }

        this.placesClient = Places.createClient(activity);

        this.model = new ContainerModel(createApiService());
    }

    //container
    private void setupContainer(Container container) {
        this.container = container;

        updateStopsInfo();

        if(!container.getDriveList().isEmpty()){
            updateRouteInfo();
        }

        RouteInfoHolder routeInfoHolder = new RouteInfoHolder();
        routeInfoHolder.setAddressList(this.container.getAddressList());
        routeInfoHolder.setDriveList(this.container.getDriveList());

        view.setupFragments(routeInfoHolder);
    }

    private void updateStopsInfo() {

        int privateAddressCount = container.getPrivateAddressCount();
        int businessAddressCount = container.getBusinessAddressCount();

        view.updateAddressCount(privateAddressCount, businessAddressCount);
    }

    @SuppressLint("DefaultLocale")
    private void updateRouteInfo() {
        boolean displayEndTimeDiff = false;
        if (container.getDriveList().size() > 0) {
            Drive finalDrive = container.getDriveList().get(container.getDriveList().size() - 1);

            long routeDistanceInM = 0;
            long routeDurationInSec = 0;
            long timeDifference = 0;

            for(Drive drive: container.getDriveList()){
                routeDistanceInM += drive.getDriveDistanceInMeters();
                routeDurationInSec += drive.getDriveDurationInSeconds();

                if(drive.getTimeDiffLong() > 0){
                    if(drive.getTimeDiffString().contains("+")){
                        timeDifference -= drive.getTimeDiffLong();
                    }else{
                        timeDifference += drive.getTimeDiffLong();
                    }
                }
            }

            String diffSign;
            String color;

            if(timeDifference < 0){
                diffSign = "+";
                timeDifference = timeDifference * -1;
                color = "red";
            }else{
                diffSign = "-";
                color = "green";
            }

            String endTimeDifference;

            if(timeDifference >= 3600000){
                endTimeDifference = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(timeDifference),
                        TimeUnit.MILLISECONDS.toMinutes(timeDifference) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDifference)),
                        TimeUnit.MILLISECONDS.toSeconds(timeDifference) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDifference)));
            }else{
                endTimeDifference = minutesFormat.format(timeDifference);
            }

            double routeDistanceInKM = (double) (routeDistanceInM/1000);
            double routeDurationInMin = (double) (routeDurationInSec/60);

            int hour;
            double minutes;
            String timeString;

            if(routeDurationInMin > 60){
                hour = (int)(routeDurationInMin/60);
                minutes = ((routeDurationInMin/60) - hour) * 60;
                String minutesString = String.format("%.0f", minutes);
                timeString = hour +" u " + minutesString + " m";
            }else{
                timeString = String.format("%.0f", routeDurationInMin) + " m";
            }

            String distanceString = String.format("%.1f", routeDistanceInKM) + " km";

            if(timeDifference != 0){
                displayEndTimeDiff = true;
            }

            view.updateRouteTravelInfo(distanceString, timeString, finalDrive.getDeliveryTimeHumanReadable(), diffSign+" "+endTimeDifference, color, displayEndTimeDiff);
        } else {
            view.updateRouteTravelInfo("0", "0", "--:--", "", "", false);
        }
    }

    @Override
    public void getUserLocation() {
        ActivityCompat.requestPermissions(activity, new String[]{ACCESS_FINE_LOCATION}, 1);
        LocationManager locationManager = new LocationManager(context, this);
        locationManager.getUserLocation();
    }

    @Override
    public void updateUserLocation(String userAddress, LatLng userLocation) {
        Address address = new Address();
        address.setValid(true);
        address.setAddress(userAddress);
        address.setLat(userLocation.latitude);
        address.setLng(userLocation.longitude);
        address.setUserLocation(true);
        this.userLocation = address;
        createEvent("mapFragment", "markAddress", address ,this);
    }

    @Override
    public void getPrediction(String address) {

        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        // Create a RectangularBounds object.
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363), //dummy lat/lng
                new LatLng(-33.858754, 151.229596));
        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                .setLocationBias(bounds)
                //.setLocationRestriction(bounds)
                .setCountry("nl")
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(address)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
            //Log.d(debugTag, "onSuccess");

            view.setupPredictionAdapter(response.getAutocompletePredictions());

            if(response.getAutocompletePredictions().size() < 1){
                view.showManualInputOption(true);
            }else{
                view.showManualInputOption(false);
            }
        }).addOnFailureListener(exception -> {
            Log.d(debugTag, "onFailure");
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.d(debugTag, "Place not found: " + apiException.getStatusCode());
            }
        });
    }

    @Override
    public void logOut() {
        endSession(session);
        view.showLoginScreen();
        view.closeActivity();
    }

    //fragment interaction

    @Override
    public void publishEvent(Event event) {
        view.postEvent(event);
    }

    @Override
    public void eventReceived(Event event) {

        if(!(event.getReceiver().equals("container") || event.getReceiver().equals("all"))){
            return;
        }

//        Log.d(debugTag, "Event received on container: "+ event.getEventName());

        switch (event.getEventName()) {
            case "itemClick":
                for(Address address : container.getAddressList()){
                    if(address.getAddress().equals(event.getAddress().getAddress())){
                        showAddressDetails(address);
                        view.showAddressDetails();
                        break;
                    }
                }
                break;
            case "setupRouteOrder":
                setupRouteOrder(event.getRouteOrder());
                break;
            case "updateApiDriveList":
                updateApiDriveList();
                break;
            case "showMap":
                showMap();
                break;
            case "getUserLocation":
                getUserLocation();
                break;
            case "showInputField":
                showInputField();
                break;
            case "removeAddress":
                removeAddress(event.getAddress());
                break;
            case "hideAddressDetails":
                view.hideAddressDetails();
                break;
            case "getDrive":
                getDrive(event.getDriveRequest());
                break;
            case "optimiseRoute":
                getOrganizedRoute();
                break;
            case "updateEndTime":
                updateRouteInfo();
                break;
            case "driveDirections":
                view.navigateToDestination(event.getDrive());
                break;
            case "addressTypeChange":

                //Log.d(debugTag, "Changing");

                if(event.getAddress().isBusiness()){
                    container.setBusinessAddressCount(container.getBusinessAddressCount()+1);
                    container.setPrivateAddressCount(container.getPrivateAddressCount()-1);
                }else{
                    container.setPrivateAddressCount(container.getPrivateAddressCount()+1);
                    container.setBusinessAddressCount(container.getBusinessAddressCount()-1);
                }

                updateStopsInfo();

                break;
        }
    }

    private void setupRouteOrder(List<Address> routeOrder){
        this.routeOrder = routeOrder;
    }

    private void showInputField(){
        view.showInputField();
    }

    private void showMap() {
        view.showFragment(1);
    }

    private void showAddressDetails(Address address){
        createEvent("addressDetails", "addressClicked", address, this);
    }

    @Override
    public void updateCommentsList() {
        createEvent("addressDetails", "updateCommentsList",this);
    }

    private void addAddress(Address address) {
        if(address.isValid()){

            boolean notFound = true;
            for(Address it : container.getAddressList()){
                if (it.getAddress().equals(address.getAddress())) {
                    notFound = false;
                    break;
                }
            }
            if(notFound){
                if(address.isBusiness()){
                    container.setBusinessAddressCount(container.getBusinessAddressCount()+1);
                }else{
                    container.setPrivateAddressCount(container.getPrivateAddressCount()+1);
                }

                updateStopsInfo();
            }

            createEvent("addressDetails", "addressAdded", address, this);
            view.showNewAddressDetails();
            createEvent("addressFragment", "addAddress", address, this);
        }else{
            view.showDialog(address.getAddress() + " is invalid");
            view.showInputField();
        }
    }

    private void addDrive(Drive drive) {
        if(drive != null && drive.isValid()){
            createEvent("driveFragment", "addDrive", drive, this);
        }else{
            createEvent("mapFragment", "driveFailed",this);
        }
    }


    //model request
    @Override
    public void getContainer() {
//        model.containerRequest(session.getUsername(), ContainerController.this);
        handler.postDelayed(() -> model.containerRequest(session.getUsername(), ContainerController.this), 500);
    }

    @Override
    public void getAddress(String address) {
        AddressRequest request = new AddressRequest(session.getUsername(), address);
        model.addressRequest(request, ContainerController.this);
    }

    @Override
    public void getOrganizedRoute() {

        if(container.getAddressList().isEmpty()){
            view.showDialog("There are no addresses to sort");
            return;
        }else if(routeOrder.size() == container.getAddressList().size()){
            view.showDialog("Nothing to sort");
            return;
        }

        view.showOptimisingDialog(true);

        createEvent("mapFragment","organizingRoute", true, this);

        OrganizeRouteRequest request = new OrganizeRouteRequest();
        request.setUsername(session.getUsername());
        request.setRouteList(new ArrayList<>());
        if (routeOrder.size() > 0) {
            for(Address address : routeOrder){
                request.getRouteList().add(address.getAddress());
            }
        } else {
            request.getRouteList().add(userLocation.getAddress());
        }

        ((MyApplication) this.activity.getApplication()).setOrganizing(true);
        model.sortRequest(request, this);
    }

    private void updateApiDriveList(){

        if(!updatingApiDriveList){
//            Log.d(debugTag, "Updating drive list in 30 sec");

            UpdateDriveListRequest request = new UpdateDriveListRequest();
            request.setUsername(session.getUsername());
            request.setDriveList(container.getDriveList());

            handler.postDelayed(() -> {
                model.updateApiDriveList(request);
                updatingApiDriveList = false;
            }, 30000);
            updatingApiDriveList = true;
        }
    }

    private void removeAddress(Address address) {
        RemoveAddressRequest request = new RemoveAddressRequest();
        request.setUsername(session.getUsername());
        request.setAddress(address.getAddress());

        if(address.isBusiness()){
            container.setBusinessAddressCount(container.getBusinessAddressCount()-1);
        }else{
            container.setPrivateAddressCount(container.getPrivateAddressCount()-1);
        }

        updateStopsInfo();

        model.removeAddress(request);
    }

    private void getDrive(DriveRequest request) {
        createEvent("addressFragment", "gettingDrive", true, this);
        model.driveRequest(request, this);
    }

    //model response

    @Override
    public void containerResponse(Container response) {
        if (response != null) {
            setupContainer(response);
        }else{
            view.hideLoader();
        }
    }

    @Override
    public void containerResponseFailure() {
        view.hideLoader();
    }

    @Override
    public void addressResponse(Address response) {
        view.hideLoader();
        if (response != null) {
            addAddress(response);
        }else{
            view.showToast("Unable to fetch address from api");
        }
    }

    @Override
    public void addressResponseFailure() {
        view.hideLoader();
        view.showDialog("Unable to add the address, please try again");
//        view.showToast("Unable to fetch address from api");
    }

    @Override
    public void organizeRouteResponse(OrganizedRouteResponse response) {
        view.showOptimisingDialog(false);
        if(response != null){
            RouteInfo routeInfo = new RouteInfo();
            routeInfo.setDriveList(response.getOrganizedRoute());
            createEvent("mapFragment", "updateRouteOrder", routeInfo, this);
            createEvent("driveFragment", "updateDriveList", routeInfo, this);
            createEvent("mapFragment","organizingRoute", false, this);
        }else{
            view.showDialog("Unable to sort addresses, please try again");
        }
        ((MyApplication) this.activity.getApplication()).setOrganizing(false);
    }

    @Override
    public void organizedRouteFailure() {
        view.showOptimisingDialog(false);
        view.showDialog("Unable to sort addresses, please try again");
        createEvent("mapFragment","organizingRoute", false, this);
        ((MyApplication) this.activity.getApplication()).setOrganizing(false);
    }

    @Override
    public void driveResponse(Drive response) {
        createEvent("addressFragment", "gettingDrive", false, this);
        addDrive(response);
    }

    @Override
    public void driveResponseFailure() {
        createEvent("addressFragment", "gettingDrive", false, this);
        addDrive(null);
    }
}