package com.example.routeplanner.features.container;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import com.example.routeplanner.data.api.ApiCallback;
import com.example.routeplanner.data.api.ApiService;
import com.example.routeplanner.data.models.LocationManager;
import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.Event;
import com.example.routeplanner.data.pojos.RouteInfoHolder;
import com.example.routeplanner.data.pojos.Session;
import com.example.routeplanner.data.pojos.api.AddressRequest;
import com.example.routeplanner.data.pojos.api.Container;
import com.example.routeplanner.data.pojos.api.Drive;
import com.example.routeplanner.data.pojos.api.DriveRequest;
import com.example.routeplanner.data.pojos.api.RemoveAddressRequest;
import com.example.routeplanner.features.shared.BaseController;
import com.example.routeplanner.features.shared.MvcBaseController;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ContainerController extends BaseController implements
        MvcBaseController,
        MvcContainer.Controller,
        ApiCallback.ContainerResponseCallback,
        ApiCallback.AddAddressCallback,
        ApiCallback.DriveResponseCallback {

    private final String debugTag = "debugTag";

    private MvcContainer.View view;

    private ContainerActivity activity;

    private ContainerModel model;

    private Context context;
    private Session session;
    private Container container;
    private PlacesClient placesClient;

    ContainerController(MvcContainer.View view, ContainerActivity activity) {
        this.view = view;
        this.activity = activity;


        Retrofit retrofit = new Retrofit.Builder()
                .client(new OkHttpClient())//192.168.0.16 - 217.103.231.118
                .baseUrl("http://217.103.231.118:8080/webapi/")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();

        this.model = new ContainerModel(retrofit.create(ApiService.class));
    }

    //container data

    @Override
    public void setVariables(Session session, Context context, PlacesClient placeClient) {
        this.session = session;
        this.context = context;
        this.placesClient = placeClient;

    }

    private void setupContainer(Container container) {
        this.container = container;

        updateContainerInfo();

        RouteInfoHolder routeInfoHolder = new RouteInfoHolder();
        routeInfoHolder.setAddressList(container.getAddressList());
        routeInfoHolder.setDriveList(container.getDriveList());

        view.setupFragments(routeInfoHolder);
    }

    private void updateContainerInfo() {

        int privateAddressCount = container.getPrivateAddressCount();
        int businessAddressCount = container.getBusinessAddressCount();
        int invalidAddressCount = container.getInvalidAddressCount();

//        In this method update the container by loading the values that are displayed in the top information bar

//        deliveryCompletion[0] = deliveredPrivate;
//        deliveryCompletion[2] = deliveredBusiness;
//        deliveryCompletion[1] = route.getPrivateAddressCount();
//        deliveryCompletion[3] = route.getBusinessAddressCount();
//        view.updateDeliveryCompletion(deliveryCompletion);
    }

    private void updateRouteEndTime() {
        if (container.getDriveList().size() > 0) {
            Drive finalDrive = container.getDriveList().get(container.getDriveList().size() - 1);
            view.updateRouteEndTimeTv(finalDrive.getDeliveryTimeHumanReadable());
        } else {
            view.updateRouteEndTimeTv("");
        }
    }

    @Override
    public void updateUserLocation(String userAddress, LatLng userLocation) {
        Address address = new Address();
        address.setValid(true);
        address.setAddress(userAddress);
        address.setLat(userLocation.latitude);
        address.setLng(userLocation.longitude);
        address.setUserLocation(true);
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

        placesClient.findAutocompletePredictions(request).addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
            @Override
            public void onSuccess(FindAutocompletePredictionsResponse response) {
                //Log.d(debugTag, "onSuccess");
                view.setupPredictionAdapter(response.getAutocompletePredictions());

                if(response.getAutocompletePredictions().size() < 1){
                    view.showManualInputOption(true);
                }else{
                    view.showManualInputOption(false);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(debugTag, "onFailure");
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.d(debugTag, "Place not found: " + apiException.getStatusCode());
                }
            }
        });
    }

    //menu bottoms

    @Override
    public void getUserLocation() {
        ActivityCompat.requestPermissions(activity, new String[]{ACCESS_FINE_LOCATION}, 1);
        LocationManager locationManager = new LocationManager(context, this);
        locationManager.getUserLocation();
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

        if (!event.getReceiver().equals("container")) {
            return;
        }

        Log.d(debugTag, "Event received on container: "+ event.getEventName());

        switch (event.getEventName()) {
            case "updateContainer":
                getContainer();
                break;
            case "itemClick":
                for(Address address : container.getAddressList()){
                    if(address.getAddress().equals(event.getAddress().getAddress())){
                        showAddressDetails(address);
                        view.showBottomAddressDetails();
                        break;
                    }
                }
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
            case "getDrive":
                getDrive(event.getDriveRequest());
                break;
            case "updateEndTime":
                updateRouteEndTime();
                break;
            case "driveDirections":
                view.navigateToDestination(event.getAddressString());
                break;
        }
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

    private void showAddressDetailsNewAddress(Address address){
        createEvent("addressDetails", "addressAdded", address, this);
    }

    @Override
    public void updateCommentsList() {
        createEvent("addressDetails", "updateCommentsList",this);
    }

    private void addAddress(Address address) {
        if(address.isValid()){
            showAddressDetailsNewAddress(address);
            view.showTopAddressDetails();
        }
        createEvent("addressFragment", "addAddress", address, this);
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
        model.containerRequest(session.getUsername(), this);
    }

    @Override
    public void getAddress(String address) {
        AddressRequest request = new AddressRequest(session.getUsername(), address);
        model.addressRequest(request, this);
    }

    private void removeAddress(Address address) {
        RemoveAddressRequest request = new RemoveAddressRequest();
        request.setUsername(session.getUsername());
        request.setAddress(address.getAddress());
        model.removeAddress(request);
    }

    private void getDrive(DriveRequest request) {
        model.driveRequest(request, this);
    }

    //model response

    //If the server has an error and sends back a routeResponse with a html page
    //the response processing will fail! FIX THIS!!!

    @Override
    public void containerResponse(Container response) {
        if (response != null) {
            setupContainer(response);
        }
    }

    @Override
    public void containerResponseFailure() {
        view.showToast("Unable to fetch container from api");
    }

    @Override
    public void addressResponse(Address response) {
        if (response != null) {

            addAddress(response);
        }
    }

    @Override
    public void addressResponseFailure() {
        view.showToast("Unable to fetch address from api");
    }

    @Override
    public void driveResponse(Drive response) {
        addDrive(response);
    }

    @Override
    public void driveResponseFailure() {
        addDrive(null);
    }
}