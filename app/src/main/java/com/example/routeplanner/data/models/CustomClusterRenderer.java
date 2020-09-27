package com.example.routeplanner.data.models;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.api.Drive;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.List;
import java.util.Map;

public class CustomClusterRenderer extends DefaultClusterRenderer<Address> {

    private final String debugTag = "debugTag";
    private final Context context;
    private List<Address> routeOrder;
    private List<Drive> driveList;

    public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<Address> clusterManager, List<Address> routeOrder, List<Drive>  driveList) {
        super(context, map, clusterManager);
        this.context = context;
        this.routeOrder = routeOrder;
        this.driveList = driveList;
    }

    @Override
    protected void onClusterItemRendered(Address address, Marker marker) {
        changeMarkerIcon(address);
    }

    public void changeMarkerIcon(Address address){

        String iconName;
        Marker marker = getMarker(address);

        if(address.isFetchingDriveInfo()){
            iconName = "clock";
        }else{

            if(address.isSelected()){

                int arrivalTime = 0;
                int openingTime = address.getOpeningTime();
                int closingTime = address.getClosingTime();

                for(Drive drive : driveList){

                    if(drive.getDestinationAddress().getAddress().equals(address.getAddress())){

                        String[] deliveryTime = drive.getDeliveryTimeHumanReadable().split(":");
                        String hourString = deliveryTime[0];
                        String minuteString = deliveryTime[1];

                        int hour = Integer.parseInt(hourString);
                        int minute = Integer.parseInt(minuteString);

                        arrivalTime = (hour*60) + minute;

                    }
                }

                if(address.isBusiness() && openingTime>0 && closingTime>0){
                    if(arrivalTime > address.getOpeningTime() && arrivalTime < address.getClosingTime()){
                        iconName = "marker_pending_"+String.valueOf(routeOrder.indexOf(address)+1);
                    }else{
                        iconName = "marker_invalid_"+String.valueOf(routeOrder.indexOf(address)+1);
                    }
                }else{
                    iconName = "marker_pending_"+String.valueOf(routeOrder.indexOf(address)+1);
                }

            }else{

                if(address.isCompleted()){
                    address.setCompleted(false);
                }

                if(address.isBusiness()){
                    iconName = "marker_company";
                }else{
                    iconName = "marker_house";
                }
            }
        }

        if(address.isCompleted()){
            iconName = "marker_done_"+String.valueOf(routeOrder.indexOf(address)+1);
        }

        if(address.isUserLocation()){
            iconName = "user_location";
        }

        Resources res = context.getResources();
        //Log.d(debugTag, "iconName: " + iconName);
        int resID = res.getIdentifier(iconName, "drawable", context.getPackageName());
        try{
            //Log.d(debugTag, "ResID: " + resID);
            marker.setIcon(BitmapDescriptorFactory.fromResource(resID));
        }catch (NullPointerException e){
            Log.d(debugTag, "CustomClusterRenderer changeMarkerIcon: " + e.getMessage());
        }
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<Address> cluster) {
        return false;
    }
}