package com.example.routeplanner.data.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.routeplanner.data.pojos.api.Drive;

import java.util.List;

public class RouteInfoHolder implements Parcelable {

    private boolean organized;
    private Address userLocation;
    private List<Address> addressList;
    private List<Drive> driveList;
    private List<String> routeOrder;

    public RouteInfoHolder() {
    }

    private RouteInfoHolder(Parcel in) {
        organized = in.readByte() != 0;
        userLocation = in.readParcelable(Address.class.getClassLoader());
        addressList = in.createTypedArrayList(Address.CREATOR);
        routeOrder = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (organized ? 1 : 0));
        dest.writeParcelable(userLocation, flags);
        dest.writeTypedList(addressList);
        dest.writeStringList(routeOrder);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RouteInfoHolder> CREATOR = new Creator<RouteInfoHolder>() {
        @Override
        public RouteInfoHolder createFromParcel(Parcel in) {
            return new RouteInfoHolder(in);
        }

        @Override
        public RouteInfoHolder[] newArray(int size) {
            return new RouteInfoHolder[size];
        }
    };

    public boolean isOrganized() {
        return organized;
    }

    public void setOrganized(boolean organized) {
        this.organized = organized;
    }

    public Address getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(Address userLocation) {
        this.userLocation = userLocation;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    public List<Drive> getDriveList() {
        return driveList;
    }

    public void setDriveList(List<Drive> driveList) {
        this.driveList = driveList;
    }

    public List<String> getRouteOrder() {
        return routeOrder;
    }

    public void setRouteOrder(List<String> routeOrder) {
        this.routeOrder = routeOrder;
    }
}
