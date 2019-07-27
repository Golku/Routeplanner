package com.example.routeplanner.data.models;

import android.content.Context;
import android.content.res.Resources;

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
            iconName = "time_ic";
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
                        iconName = "ic_pending_marker_"+String.valueOf(routeOrder.indexOf(address)+1);
                    }else{
                        iconName = "ic_invalid_marker_"+String.valueOf(routeOrder.indexOf(address)+1);
                    }
                }else{
                    iconName = "ic_pending_marker_"+String.valueOf(routeOrder.indexOf(address)+1);
                }

            }else{
                if(address.isBusiness()){
                    iconName = "business_ic";
                }else{
                    iconName = "home_ic";
                }
            }
        }

        if(address.isCompleted()){
            iconName = "ic_done_marker_"+String.valueOf(routeOrder.indexOf(address)+1);
        }

        if(address.isUserLocation()){
            iconName = "ic_marker_origin";
        }

        Resources res = context.getResources();
        int resID = res.getIdentifier(iconName, "drawable", context.getPackageName());
        marker.setIcon(BitmapDescriptorFactory.fromResource(resID));
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<Address> cluster) {
        return false;
    }
}