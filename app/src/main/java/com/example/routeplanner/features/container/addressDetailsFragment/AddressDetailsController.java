package com.example.routeplanner.features.container.addressDetailsFragment;

import android.os.Handler;
import android.util.Log;
import com.example.routeplanner.data.database.DatabaseCallback;
import com.example.routeplanner.data.database.DatabaseService;
import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.CommentInformation;
import com.example.routeplanner.data.pojos.Event;
import com.example.routeplanner.data.pojos.Session;
import com.example.routeplanner.data.pojos.database.AddressInformationResponse;
import com.example.routeplanner.data.pojos.database.AddressTypeResponse;
import com.example.routeplanner.features.splash.SplashActivity;
import com.google.gson.Gson;
import org.greenrobot.eventbus.EventBus;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddressDetailsController implements MvcAddressDetails.Controller,
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

    AddressDetailsController(MvcAddressDetails.View view, Session session) {
        this.view = view;
        this.session = session;

        Retrofit retrofit = new Retrofit.Builder()
                .client(new OkHttpClient())
                .baseUrl("http://217.103.231.118/map/v1/")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();

        adapter = new AddressDetailsAdapter(null, this);
        this.view.setUpAdapter(adapter);
        this.model = new AddressDetailsModel(retrofit.create(DatabaseService.class));
        this.address = new Address();
        this.handler = new Handler();
    }

    @Override
    public void getAddressInformation() {
        view.networkOperationStarted();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                model.getAddressInformation(address, AddressDetailsController.this);
            }
        }, 1000);
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
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                model.changeAddressType(session.getUsername(), address, AddressDetailsController.this);
            }
        }, 1000);
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
    public void addCommentButtonClick() {
        view.showCommentInput(address);
    }

    @Override
    public void eventReceived(Event event) {

        if (!event.getReceiver().equals("addressDetails")) {
            return;
        }

        Log.d(debugTag, "Event received on addressDetails: "+ event.getEventName());

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
    public void onAddressInformationResponse(AddressInformationResponse response) {
        view.networkOperationFinish(response.getMessage());
        if (response.isInformationAvailable()) {
            if (response.getAddressInformation() != null) {
                view.setUpAdapter(adapter = new AddressDetailsAdapter(response.getAddressInformation(), this));
                view.scrollToComment(response.getAddressInformation().getCommentsCount());
            }
        }
    }

    @Override
    public void onAddressInformationResponseFailure() {
        view.networkOperationFinish("Failed to get address information");
        view.showToast("Unable to connect to the database");
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

            Event event = new Event();
            event.setReceiver("all");
            event.setEventName("addressTypeChange");
            event.setAddress(address);

            EventBus.getDefault().post(event);
        }

        view.changeAddressType(address);
        view.networkOperationFinish("typeChange");
    }

    @Override
    public void typeChangeResponseFailure() {
        view.networkOperationFinish("Failed to change address");
        view.showToast("Fail to change address type");
    }
}
