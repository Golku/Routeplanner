package com.example.routeplanner.data.models;

import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.api.Drive;
import com.example.routeplanner.features.container.driveListFragment.DriveListAdapter;
import com.example.routeplanner.features.container.driveListFragment.DriveListController;

import java.text.SimpleDateFormat;
import java.util.List;

public class DriveListHandler {

    private final String debugTag = "debugTag";

    private List<Drive> driveList;
    private DriveListAdapter adapter;
    private DriveListController callback;

    public DriveListHandler(List<Drive> driveList, DriveListController callback) {
        this.driveList = driveList;
        this.callback = callback;
    }

    public void createAdapter(){
        adapter = new DriveListAdapter(callback, driveList);
    }

    public DriveListAdapter getAdapter(){
        return adapter;
    }

    public int getListSize(){
        return driveList.size();
    }

    public void addressTypeChange(Address address) {

        for (Drive drive : driveList) {

            if (drive.getDestinationAddress().getAddress().equals(address.getAddress())) {

                if (address.isBusiness()) {
                    drive.setDestinationIsABusiness(true);
                    drive.getDestinationAddress().setBusiness(true);
                } else {
                    drive.setDestinationIsABusiness(false);
                    drive.getDestinationAddress().setBusiness(false);
                }

                break;
            }
        }
    }

    public void addDriveToList(Drive drive) {
        driveList.add(drive);

        long deliveryTime;
        long driveTime = drive.getDriveDurationInSeconds() * 1000;
        long PACKAGE_DELIVERY_TIME = 120000;
        SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");

        if (driveList.size() > 1) {
            Drive previousDrive = driveList.get(driveList.indexOf(drive) - 1);
            deliveryTime = previousDrive.getDeliveryTimeInMillis() + driveTime + PACKAGE_DELIVERY_TIME;
        } else {
            long date = System.currentTimeMillis();
            deliveryTime = date + driveTime + PACKAGE_DELIVERY_TIME;
        }

        String deliveryTimeString = sdf.format(deliveryTime);

        drive.setPosition(driveList.size());
        drive.setDeliveryTimeInMillis(deliveryTime);
        drive.setDeliveryTimeHumanReadable(deliveryTimeString);

        adapter.notifyItemInserted(driveList.indexOf(drive));
    }

    private void updateDeliveryTime(Drive drive) {

        long deliveryTime;
        long driveTime = drive.getDriveDurationInSeconds() * 1000;
        long PACKAGE_DELIVERY_TIME = 120000;
        SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");

        Drive previousDrive = driveList.get(driveList.indexOf(drive) - 1);

        if (previousDrive.getDone() == 1) {
            long date = System.currentTimeMillis();
            deliveryTime = date + driveTime + PACKAGE_DELIVERY_TIME;
        } else {
            deliveryTime = previousDrive.getDeliveryTimeInMillis() + driveTime + PACKAGE_DELIVERY_TIME;
        }

        String deliveryTimeString = sdf.format(deliveryTime);

        drive.setDeliveryTimeInMillis(deliveryTime);
        drive.setDeliveryTimeHumanReadable(deliveryTimeString);
    }

    public boolean driveCompleted(Drive drive){

        int position = driveList.indexOf(drive);
        boolean completed;

        if(position == 0){

            drive.setDone(1);

            for(Drive it : driveList){
                if(it.getDone() == 0){
                    updateDeliveryTime(it);
                }
            }

            completed = true;

        }else{
            Drive previousDrive = driveList.get(position - 1);

            if(previousDrive.getDone() == 1){

                drive.setDone(1);

                for(Drive it : driveList){
                    if(it.getDone() == 0){
                        updateDeliveryTime(it);
                    }
                }

                completed = true;
            }else{
                completed = false;
            }

        }

        if(completed){
            calculateTimeDiff(drive);
        }

        adapter.notifyDataSetChanged();

        return completed;
    }

    private void calculateTimeDiff(Drive drive){
        long timeDif = drive.getDeliveryTimeInMillis() - System.currentTimeMillis();

        String diffSign;

        if(timeDif < 0){
            diffSign = "+";
            timeDif = timeDif * -1;
        }else{
            diffSign = "-";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        String deliveryTimeDifference = sdf.format(timeDif);

        drive.setTimeDiffLong(timeDif);
        drive.setTimeDiffString(diffSign+" "+deliveryTimeDifference);
    }

    public void removeDriveFromList() {
        int position = driveList.size() - 1;
        driveList.remove(position);
        adapter.notifyItemRemoved(position);
    }

    public void removeMultipleDrive(String address) {

        for (Drive drive : driveList) {
            if (address.equals(drive.getDestinationAddress().getAddress())) {
                driveList.subList(driveList.indexOf(drive), driveList.size()).clear();
                break;
            }
        }
    }
}
