package com.example.routeplanner.features.container.addressListFragment;

import android.util.Log;
import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.Event;
import com.example.routeplanner.features.shared.BaseController;
import com.example.routeplanner.features.shared.MvcBaseController;

import java.util.List;

public class AddressListController extends BaseController implements
        MvcBaseController,
        MvcAddressList.Controller,
        AddressListAdapter.AdapterCallback{

    private final String debugTag = "debugTag";

    private MvcAddressList.View view;

    private List<Address> addressList;
    private AddressListAdapter adapter;

    private boolean gettingDrive;
    private boolean addressRestored;
    private int deletedItemPosition;
    private Address deletedAddress;

    AddressListController(MvcAddressList.View view, List<Address> addressList) {
        this.view = view;
        this.addressList = addressList;
        addressRestored = false;
    }

    @Override
    public void showAddressList() {
        adapter = new AddressListAdapter(this, addressList);
        view.setupAdapter(adapter);
    }

    @Override
    public void showInputField() {

        if(view.isOrganising()){
            view.showDialog("Can't add new addresses while organising");
            return;
        }

        createEvent("container", "showInputField", this);
    }

    @Override
    public void itemClick(Address address) {
        createEvent("container", "itemClick", address,this);
    }

    @Override
    public void showAddress(Address address) {
        createEvent("container", "showMap", this);
        createEvent("mapFragment", "showMarker", address, this);
    }

    @Override
    public void removeAddress(Address address) {

        if(gettingDrive){
            view.showDialog("Can't delete now, getting drive");
//            view.showToast("Can't delete now, getting drive");
            adapter.notifyDataSetChanged();
            return;
        }else if (view.isOrganising()){
            view.showDialog("Can't delete address while organizing");
            adapter.notifyDataSetChanged();
            return;
        }

        deletedItemPosition = addressList.indexOf(address);
        deletedAddress = address;
        adapter.notifyItemRemoved(addressList.indexOf(address));
        addressList.remove(address);
        view.addressDeleted(deletedItemPosition, address);
    }

    @Override
    public void restoreAddress() {
        addressRestored = true;
        addressList.add(deletedItemPosition, deletedAddress);
        adapter.notifyItemInserted(deletedItemPosition);
    }

    @Override
    public void removeAddressFromContainer(Address address) {
        if(!addressRestored){
            createEvent("mapFragment", "removeMarker", address,this);
            createEvent("container", "removeAddress", address,this);
        }
        addressRestored = false;
    }

    @Override
    public void eventReceived(Event event) {

        if(!(event.getReceiver().equals("addressFragment") || event.getReceiver().equals("all"))){
            return;
        }

        //Log.d(debugTag, "Event received on addressFragment: "+ event.getEventName());

        switch (event.getEventName()) {
            case "addressTypeChange" : addressTypeChange(event.getAddress());
                break;
            case "openingTimeChange" : openingTimeChange(event.getAddress());
                break;
            case "closingTimeChange" : closingTimeChange(event.getAddress());
                break;
            case "addAddress" : addAddress(event.getAddress());
                break;
            case "gettingDrive" : gettingDrive(event.isGettingDrive());
                break;
            case "updateAddressList" : showAddressList();
                break;
        }
    }

    private void addressTypeChange(Address address){
        for(Address it: addressList){
            if(it.getAddress().equals(address.getAddress())){
                it.setBusiness(address.isBusiness());

                if(it.isBusiness()){
                    if(it.getOpeningTime() == 0){
                        it.setOpeningTime(480);
                    }

                    if(it.getClosingTime() == 0){
                        it.setClosingTime(1020);
                    }
                }

                showAddressList();
                break;
            }
        }
    }

    private void openingTimeChange(Address address) {
        for(Address it: addressList){
            if(it.getAddress().equals(address.getAddress())){
                it.setOpeningTime(address.getOpeningTime());
                break;
            }
        }
        createEvent("mapFragment","updateMarkers", this);
    }

    private void closingTimeChange(Address address) {
        for(Address it: addressList){
            if(it.getAddress().equals(address.getAddress())){
                it.setClosingTime(address.getClosingTime());
                break;
            }
        }
        createEvent("mapFragment","updateMarkers", this);
    }

    private void addAddress(Address address){

        boolean notFound = true;
        for(Address it : addressList){
            if(it.getAddress().equals(address.getAddress())){
                addressList.set(addressList.indexOf(it), address);
                notFound = false;
                adapter.notifyDataSetChanged();
                break;
            }
        }
        if(notFound){
            addressList.add(address);
            adapter.notifyItemInserted(addressList.indexOf(address));
            view.scrollToItem(addressList.size());
            createEvent("mapFragment","markAddress", address, this);
        }
    }

    protected void gettingDrive(boolean gettingDrive){
        this.gettingDrive = gettingDrive;
    }

    @Override
    public void publishEvent(Event event) {
        view.postEvent(event);
    }
}