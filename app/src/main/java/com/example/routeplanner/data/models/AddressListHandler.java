package com.example.routeplanner.data.models;

import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.features.container.addressListFragment.AddressListAdapter;
import com.example.routeplanner.features.container.addressListFragment.AddressListController;

import java.util.List;

public class AddressListHandler {

    private AddressListAdapter adapter;

    public AddressListHandler(List<Address> addressList, AddressListController callback) {
        adapter = new AddressListAdapter(callback, addressList);
    }

    public AddressListAdapter getAdapter(){
        return adapter;
    }
}
