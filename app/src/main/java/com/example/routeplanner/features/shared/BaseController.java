package com.example.routeplanner.features.shared;

import android.util.Log;

import com.example.routeplanner.data.api.ApiService;
import com.example.routeplanner.data.database.DatabaseService;
import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.Event;
import com.example.routeplanner.data.pojos.RouteInfo;
import com.example.routeplanner.data.pojos.Session;
import com.example.routeplanner.data.pojos.api.ChangeAddressRequest;
import com.example.routeplanner.data.pojos.api.Drive;
import com.example.routeplanner.data.pojos.api.DriveRequest;
import com.google.gson.Gson;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class BaseController {
    private final String debugTag = "debugTag";

    private final long TIME_OUT = 3600000; //60 min
//    private final long TIME_OUT = 28800000; //8 hours
//    private final long TIME_OUT = 120000; //120 sec
//    private final long TIME_OUT = 60000; //60 sec

    private Long currentTime;

    protected BaseController() {
        this.currentTime = System.currentTimeMillis();
    }

    protected void beginSession(int userId, String username, Session session){
        session.setActive(true);
        session.setUserId(userId);
        session.setUsername(username);
        session.setLoginTime(currentTime);
    }

    protected void endSession(Session session){
        session.setActive(false);
    }

    protected boolean verifySession(Session session){
        return session.getActive();
    }

    protected boolean verifySessionTimeOut(Session session){
        //Log.d(debugTag, "Remaining seconds: "+ String.valueOf(((session.getLoginTime() + TIME_OUT) - currentTime)/1000));
        return currentTime < (session.getLoginTime() + TIME_OUT);
    }

    protected ApiService createApiService(){
        Retrofit retrofit = new Retrofit.Builder()
                .client(new OkHttpClient())//192.168.0.16 - 217.103.231.118
                .baseUrl("http://212.187.39.139:8080/RouteApi_war/webapi/")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
        return retrofit.create(ApiService.class);
    }

    protected DatabaseService createDatabaseService(){
        Retrofit retrofit = new Retrofit.Builder()
                .client(new OkHttpClient())
                .baseUrl("http://212.187.39.139/map/v1/")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
        return retrofit.create(DatabaseService.class);
    }

    protected void createEvent(String receiver, String eventName, MvcBaseController callback){
        Event event = new Event();
        event.setReceiver(receiver);
        event.setEventName(eventName);
        callback.publishEvent(event);
    }

    protected void createEvent(String receiver, String eventName, boolean val, MvcBaseController callback){
        Event event = new Event();
        event.setReceiver(receiver);
        event.setEventName(eventName);
        event.setGettingDrive(val);
        event.setOrganizingRoute(val);
        callback.publishEvent(event);
    }

    protected void createEvent(String receiver, String eventName, String address, MvcBaseController callback){
        Event event = new Event();
        event.setReceiver(receiver);
        event.setEventName(eventName);
        event.setAddressString(address);
        callback.publishEvent(event);
    }

    protected void createEvent(String receiver, String eventName, Address address, MvcBaseController callback){
        Event event = new Event();
        event.setReceiver(receiver);
        event.setEventName(eventName);
        event.setAddress(address);
        callback.publishEvent(event);
    }

    protected void createEvent(String receiver, String eventName, List<Address> routeOrder, MvcBaseController callback){
        Event event = new Event();
        event.setReceiver(receiver);
        event.setEventName(eventName);
        event.setRouteOrder(routeOrder);
        callback.publishEvent(event);
    }

    protected void createEvent(String receiver, String eventName, RouteInfo routeInfo, MvcBaseController callback){
        Event event = new Event();
        event.setReceiver(receiver);
        event.setEventName(eventName);
        event.setRouteInfo(routeInfo);
        callback.publishEvent(event);
    }

    protected void createEvent(String receiver, String eventName, Drive drive, MvcBaseController callback){
        Event event = new Event();
        event.setReceiver(receiver);
        event.setEventName(eventName);
        event.setDrive(drive);
        callback.publishEvent(event);
    }

    protected void createEvent(String receiver, String eventName, DriveRequest request, MvcBaseController callback){
        Event event = new Event();
        event.setReceiver(receiver);
        event.setEventName(eventName);
        event.setDriveRequest(request);
        callback.publishEvent(event);
    }
}