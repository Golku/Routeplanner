package com.example.routeplanner.features.container.addressDetailsFragment;

import android.os.Handler;
import android.util.Log;
import com.example.routeplanner.data.database.DatabaseCallback;
import com.example.routeplanner.data.database.DatabaseService;
import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.CommentInformation;
import com.example.routeplanner.data.pojos.Event;
import com.example.routeplanner.data.pojos.Session;
import com.example.routeplanner.data.pojos.api.UpdatePackageCountRequest;
import com.example.routeplanner.data.pojos.database.AddressInformationResponse;
import com.example.routeplanner.data.pojos.database.AddressTypeResponse;
import com.example.routeplanner.features.shared.BaseController;
import com.example.routeplanner.features.shared.MvcBaseController;
import com.example.routeplanner.features.splash.SplashActivity;
import com.google.gson.Gson;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddressDetailsController extends BaseController implements MvcAddressDetails.Controller,
        MvcBaseController,
        DatabaseCallback.AddressInformationCallBack,
        AddressDetailsAdapter.CommentListFunctions,
        DatabaseCallback.AddressTypeChangeCallback {

    private final String debugTag = "debugTag";

    private MvcAddressDetails.View view;
    private AddressDetailsModel model;
    private Handler handler;

    private Session session;
    private Address address;

    private AddressDetailsAdapter adapter;

    private ArrayList<String> addressList;
    private ArrayList<Integer> countList;
    private boolean updatingCount;

    AddressDetailsController(MvcAddressDetails.View view, Session session) {
        this.view = view;
        this.session = session;

        adapter = new AddressDetailsAdapter(null, this);
        this.view.setUpAdapter(adapter);
        this.model = new AddressDetailsModel(createDatabaseService(), createApiService());
        this.address = new Address();
        this.handler = new Handler();
        this.addressList = new ArrayList<>();
        this.countList = new ArrayList<>();
    }

    @Override
    public void getAddressInformation() {
        view.networkOperationStarted("fetching address comments");
//        model.getAddressInformation(address, AddressDetailsController.this);
        handler.postDelayed(() -> model.getAddressInformation(address, AddressDetailsController.this), 500);
    }

    @Override
    public String convertTime(int timeInMinutes) {

        double timeFraction = ((double)timeInMinutes) / 60;

        int hour = (int) timeFraction;
        int minute = (int) ((timeFraction - hour) * 60);

        int length = String.valueOf(minute).length();

        if(length == 1){

            if(minute > 0){
                return String.valueOf(hour)+":"+"0"+(String.valueOf(minute));
            }else{
                return String.valueOf(hour)+":"+(String.valueOf(minute)+"0");
            }
        }else{
            return String.valueOf(hour)+":"+(String.valueOf(minute));
        }
    }

    @Override
    public void changeAddressType() {
//        model.changeAddressType(session.getUsername(), address, AddressDetailsController.this);
        handler.postDelayed(() -> model.changeAddressType(session.getUsername(), address, AddressDetailsController.this), 500);
    }

    @Override
    public void changeOpeningHours(int hourOfDay, int minute, String workingHours) {

        Log.d(debugTag, "WorkingHours: " + workingHours);
        Log.d(debugTag, "HourOfDay: " + workingHours);
        Log.d(debugTag, "Minute: " + workingHours);

        int timeInMinutes = ((hourOfDay*60)+minute);
        Log.d(debugTag, "TimeInMinutes: " + timeInMinutes);
        Event event = new Event();

        switch (workingHours){
            case "open" :
                address.setOpeningTime(timeInMinutes);
                model.changeOpeningTime(address, timeInMinutes);
                event.setReceiver("addressFragment");
                event.setEventName("openingTimeChange");
                event.setAddress(address);
                break;
            case "close" :
                address.setClosingTime(timeInMinutes);
                model.changeClosingTime(address, timeInMinutes);
                event.setReceiver("addressFragment");
                event.setEventName("closingTimeChange");
                event.setAddress(address);
                break;
        }
        EventBus.getDefault().post(event);
    }

    @Override
    public void googleLinkClick() {
        view.showAddressInGoogle(address);
    }

    @Override
    public void removeStop() {
        createEvent("addressFragment", "removeAddress", this.address, this);
    }

    @Override
    public void showOnMap() {
        createEvent("container", "showMap", this);
        createEvent("mapFragment", "showMarker", address, this);
    }

    @Override
    public void addCommentButtonClick() {
        view.showCommentInput(address);
    }

    @Override
    public void eventReceived(Event event) {

        if (!event.getReceiver().equals("addressDetails")) {
            return;
        }

        //Log.d(debugTag, "Event received on addressDetails: "+ event.getEventName());

        switch (event.getEventName()) {
            case "addressClicked":
                address = event.getAddress();
                view.updateAddressInfo(address, false);
                break;
            case "addressAdded":
                address = event.getAddress();
                view.updateAddressInfo(address, true);
                break;
            case "updateCommentsList":
                model.getAddressInformation(address, AddressDetailsController.this);
                break;
        }
    }

    @Override
    public void onListItemClick(CommentInformation commentInformation) {
        view.showCommentDisplay(commentInformation);
    }

    @Override
    public void publishEvent(Event event) {
        view.postEvent(event);
    }

    @Override
    public void updatePackageCount(int count) {

        if(address.getPackageCount()+count < 1){
            return;
        }

        address.setPackageCount(address.getPackageCount()+count);
        view.changePackageCountTextView(String.valueOf(address.getPackageCount()));

        if(!addressList.contains(address.getAddress())){
            addressList.add(address.getAddress());
        }
        countList.add(addressList.indexOf(address.getAddress()), address.getPackageCount());

        createEvent("addressFragment", "updateAddressList", this);

        if(!updatingCount){
            handler.postDelayed(() -> {
                UpdatePackageCountRequest request = new UpdatePackageCountRequest();
                request.setUsername(session.getUsername());
                request.setAddressList(addressList);
                request.setCountList(countList);
                model.updatePackageCount(request);
                addressList.clear();
                countList.clear();
                updatingCount = false;
            }, 10000);
            updatingCount = true;
        }
    }

    @Override
    public void onAddressInformationResponse(AddressInformationResponse response) {

        if (response.isInformationAvailable()) {
            if (response.getAddressInformation() != null) {

                if(response.getAddressInformation().getCommentsCount() > 0){
                    view.networkOperationFinish(1, "");
                }else{
                    view.networkOperationFinish(1,"no comments");
                }

                view.setUpAdapter(adapter = new AddressDetailsAdapter(response.getAddressInformation(), this));
                view.scrollToComment(response.getAddressInformation().getCommentsCount());
            }else{
                view.networkOperationFinish(1,"no comments");
            }
        }else{
            view.networkOperationFinish(1,"no comments");
        }
    }

    @Override
    public void onAddressInformationResponseFailure() {
        view.networkOperationFinish(1,"Failed to get address information");
    }

    @Override
    public void hideAddressDetails() {
        createEvent("container", "hideAddressDetails", this);
    }

    @Override
    public void typeChangeResponse(AddressTypeResponse response) {
        if (!response.isError()) {

            if (address.isBusiness()) {
                address.setBusiness(false);
            } else {
                address.setBusiness(true);

                if(address.getOpeningTime() == 0){
                    address.setOpeningTime(480);
                }

                if(address.getClosingTime() == 0){
                    address.setClosingTime(1020);
                }
            }

            createEvent("all", "addressTypeChange", this.address, this);
        }else{
            view.showToast("Failed to change address type, please try again");
        }

        view.changeAddressType(address);
        view.networkOperationFinish(2, "");
    }

    @Override
    public void typeChangeResponseFailure() {
        view.networkOperationFinish(2,"");
        view.showToast("Failed to change address type, please try again");
    }
}
