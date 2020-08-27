package com.example.routeplanner.features.container.driveListFragment;

import android.util.Log;

import com.example.routeplanner.data.models.DriveListHandler;
import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.Event;
import com.example.routeplanner.data.pojos.api.Drive;
import com.example.routeplanner.features.shared.BaseController;
import com.example.routeplanner.features.shared.MvcBaseController;

import java.util.List;

public class DriveListController extends BaseController implements
        MvcBaseController,
        MvcDriveList.Controller,
        DriveListAdapter.AdapterCallback {

    private final String debugTag = "debugTag";

    private MvcDriveList.View view;

    private DriveListHandler listHandler;

    DriveListController(MvcDriveList.View view, List<Drive> driveList) {
        this.view = view;
        listHandler = new DriveListHandler(driveList, this);
        listHandler.createAdapter();
    }

    @Override
    public void showDriveList() {
        view.setupAdapter(listHandler.getAdapter());
    }

    @Override
    public void itemClick(Address address) {
        createEvent("container", "itemClick", address, this);
    }

    @Override
    public void goButtonClick(Drive drive) {
        createEvent("container", "driveDirections", drive, this);
    }

    @Override
    public void completeDrive(Drive drive) {

        if(view.isOrganising()){
            view.showDialog("Can't complete drive while organising");
            listHandler.getAdapter().notifyDataSetChanged();
            return;
        }

        if(listHandler.driveCompleted(drive)){
            createEvent("mapFragment","driveCompleted", drive.getDestinationAddress(),this);
            createEvent("mapFragment","updateMarkers",this);
            createEvent("container", "updateEndTime", this);
        }
    }

    @Override
    public void publishEvent(Event event) {
        view.postEvent(event);
    }

    private void updateDriveList(List<Drive> driveList){
        for(Drive drive : driveList){
            listHandler.addDriveToList(drive);
        }
    }

    @Override
    public void eventReceived(Event event) {

        if (!(event.getReceiver().equals("driveFragment") || event.getReceiver().equals("all"))) {
            return;
        }

        //Log.d(debugTag, "Event received on driveFragment: " + event.getEventName());

        switch (event.getEventName()) {
            case "addressTypeChange":
                listHandler.addressTypeChange(event.getAddress());
                showDriveList();
                break;
            case "updateDriveList":
                updateDriveList(event.getRouteInfo().getDriveList());
                createEvent("container", "updateEndTime", this);
                createEvent("container", "updateApiDriveList", this);
                break;
            case "addDrive":
                listHandler.addDriveToList(event.getDrive());
                view.scrollToItem(listHandler.getListSize());
                createEvent("mapFragment", "driveSuccess", this);
                createEvent("container", "updateEndTime", this);
                createEvent("container", "updateApiDriveList", this);
                break;
            case "removeDrive":
                listHandler.removeDriveFromList();
                view.scrollToItem(listHandler.getListSize());
                createEvent("container", "updateEndTime", this);
                createEvent("container", "updateApiDriveList", this);
                break;
            case "RemoveMultipleDrive":
                listHandler.removeMultipleDrive(event.getAddressString());
                view.scrollToItem(listHandler.getListSize());
                showDriveList();
                createEvent("container", "updateEndTime", this);
                createEvent("container", "updateApiDriveList", this);
                break;
        }
    }
}
