package com.example.routeplanner.features.container;

import android.content.Context;

import com.example.routeplanner.data.api.ApiCallback;
import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.Event;
import com.example.routeplanner.data.pojos.RouteInfoHolder;
import com.example.routeplanner.data.pojos.Session;
import com.example.routeplanner.data.pojos.api.AddressRequest;
import com.example.routeplanner.data.pojos.api.ChangeAddressRequest;
import com.example.routeplanner.data.pojos.api.DriveRequest;
import com.example.routeplanner.data.pojos.api.RemoveAddressRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;

public interface MvcContainer {

    interface View{

        void setupFragments(RouteInfoHolder routeInfoHolder);

        void setupPredictionAdapter(List<AutocompletePrediction> predictions);

        void showFragment(int position);

        void updateDeliveryCompletion(int[] deliveryCompletion);

        void updateRouteEndTimeTv(String endTime);

        void postEvent(Event event);

        void showInputField();

        void showManualInputOption(boolean show);

        void showBottomAddressDetails();

        void showTopAddressDetails();

        void navigateToDestination(String address);

        void showLoginScreen();

        void showToast(String message);

        void closeActivity();
    }

    interface Controller{

        void setVariables(Session session, Context context, PlacesClient placeClient);

        void getContainer();

        void getPrediction(String address);

        void getAddress(String address);

        void getUserLocation();

        void updateUserLocation(String userAddress, LatLng userLocation);

        void updateCommentsList();

        void logOut();

        void eventReceived(Event event);
    }

    interface Model{

        void containerRequest(String username, ApiCallback.ContainerResponseCallback callback);

        void addressRequest(AddressRequest request, ApiCallback.AddAddressCallback callback);

        void removeAddress(RemoveAddressRequest request);

        void driveRequest(DriveRequest request, ApiCallback.DriveResponseCallback callback);
    }
}
