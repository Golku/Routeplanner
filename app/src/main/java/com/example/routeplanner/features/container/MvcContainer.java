package com.example.routeplanner.features.container;

import com.example.routeplanner.data.api.ApiCallback;
import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.Event;
import com.example.routeplanner.data.pojos.RouteInfoHolder;
import com.example.routeplanner.data.pojos.api.AddressRequest;
import com.example.routeplanner.data.pojos.api.Drive;
import com.example.routeplanner.data.pojos.api.DriveRequest;
import com.example.routeplanner.data.pojos.api.OrganizeRouteRequest;
import com.example.routeplanner.data.pojos.api.OrganizedRouteResponse;
import com.example.routeplanner.data.pojos.api.RemoveAddressRequest;
import com.example.routeplanner.data.pojos.api.UpdateDriveListRequest;
import com.example.routeplanner.data.pojos.api.UpdatePackageCountRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import java.util.List;

public interface MvcContainer {

    interface View{

        void showLoader(String message);

        void hideLoader();

        void setupFragments(RouteInfoHolder routeInfoHolder);

        void setupPredictionAdapter(List<AutocompletePrediction> predictions);

        void showFragment(int position);

        void updateAddressCount(int privateAddress, int businessAddress);

        void updateRouteTravelInfo(String totalRouteDistance, String totalRouteDuration, String endTime, String endTimeDifference, String color, boolean displayEndTimeDiff);

        void postEvent(Event event);

        void showInputField();

        void showManualInputOption(boolean show);

        void showAddressDetails();

        void showNewAddressDetails(Address address);

        void hideAddressDetails();

        void navigateToDestination(Drive drive);

        void showLoginScreen();

        void showOptimisingDialog(boolean show);

        void showDialog(String message);

        void showToast(String message);

        void closeActivity();
    }

    interface Controller{

        void getContainer();

        void getPrediction(String address);

        void getAddress(String address);

        void getOrganizedRoute();

        void getUserLocation();

        void showAddressDetails();

        void updateUserLocation(String userAddress, LatLng userLocation);

        void updateCommentsList();

        String convertTime(int timeInMinutes);

        void logOut();

        void eventReceived(Event event);
    }

    interface Model{

        void containerRequest(String username, ApiCallback.ContainerResponseCallback callback);

        void addressRequest(AddressRequest request, ApiCallback.AddAddressCallback callback);

        void sortRequest(OrganizeRouteRequest request, ApiCallback.OrganizeRouteCallback callback);

        void updateApiDriveList(UpdateDriveListRequest request);

        void removeAddress(RemoveAddressRequest request);

        void driveRequest(DriveRequest request, ApiCallback.DriveResponseCallback callback);
    }
}
