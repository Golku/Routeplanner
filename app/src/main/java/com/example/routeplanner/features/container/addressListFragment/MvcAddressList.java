package com.example.routeplanner.features.container.addressListFragment;

import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.Event;

public interface MvcAddressList {

    interface View{

        void setupAdapter(AddressListAdapter adapter);

        void addressDeleted(int position, Address address);

        void postEvent(Event event);

        void scrollToItem(int position);

        void showToast(String message);
    }

    interface Controller{

        void showAddressList();

        void showInputField();

        void restoreAddress();

        void removeAddressFromContainer(Address address);

        void eventReceived(Event event);
    }
}
