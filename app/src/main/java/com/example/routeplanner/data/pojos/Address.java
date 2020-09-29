package com.example.routeplanner.data.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class Address implements Parcelable, ClusterItem {

    private boolean valid;
    private String address;
    private String street;
    private String postCode;
    private String city;
    private String country;
    private double lat;
    private double lng;
    private int packageCount;
    private boolean business;
    private String businessName;
    private String[] weekdayText;
    private int openingTime;
    private int closingTime;
    private boolean userLocation;
    private boolean selected;
    private boolean completed;
    private boolean fetchingDriveInfo;

    public Address() {
    }

    protected Address(Parcel in) {
        valid = in.readByte() != 0;
        address = in.readString();
        street = in.readString();
        postCode = in.readString();
        city = in.readString();
        country = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        packageCount = in.readInt();
        business = in.readByte() != 0;
        businessName = in.readString();
        weekdayText = in.createStringArray();
        openingTime = in.readInt();
        closingTime = in.readInt();
        userLocation = in.readByte() != 0;
        selected = in.readByte() != 0;
        completed = in.readByte() != 0;
        fetchingDriveInfo = in.readByte() != 0;
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(int packageCount) {
        this.packageCount = packageCount;
    }

    public boolean isBusiness() {
        return business;
    }

    public void setBusiness(boolean business) {
        this.business = business;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String[] getWeekdayText() {
        return weekdayText;
    }

    public void setWeekdayText(String[] weekdayText) {
        this.weekdayText = weekdayText;
    }

    public int getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(int openingTime) {
        this.openingTime = openingTime;
    }

    public int getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(int closingTime) {
        this.closingTime = closingTime;
    }

    public boolean isUserLocation() {
        return userLocation;
    }

    public void setUserLocation(boolean userLocation) {
        this.userLocation = userLocation;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isFetchingDriveInfo() {
        return fetchingDriveInfo;
    }

    public void setFetchingDriveInfo(boolean fetchingDriveInfo) {
        this.fetchingDriveInfo = fetchingDriveInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (valid ? 1 : 0));
        dest.writeString(address);
        dest.writeString(street);
        dest.writeString(postCode);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeInt(packageCount);
        dest.writeByte((byte) (business ? 1 : 0));
        dest.writeString(businessName);
        dest.writeStringArray(weekdayText);
        dest.writeInt(openingTime);
        dest.writeInt(closingTime);
        dest.writeByte((byte) (userLocation ? 1 : 0));
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeByte((byte) (completed ? 1 : 0));
        dest.writeByte((byte) (fetchingDriveInfo ? 1 : 0));
    }

    @Override
    public LatLng getPosition() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }
}
