package com.example.routeplanner.features.container.addressDetailsFragment;

import com.example.routeplanner.data.database.DatabaseCallback;
import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.CommentInformation;
import com.example.routeplanner.data.pojos.Event;
import com.example.routeplanner.data.pojos.Session;

public interface MvcAddressDetails {

    interface View{

        void setUpAdapter(AddressDetailsAdapter adapter);

        void changeAddressType(Address address);

        void networkOperationStarted(String message);

        void networkOperationFinish(String message);

        void postEvent(Event event);

        void updateAddressInfo(Address address, boolean newAddress);

        void showAddressInGoogle(Address address);

        void showCommentDisplay(CommentInformation commentInformation);

        void showCommentInput(Address address);

        void scrollToComment(int position);

        void showToast(String message);
    }

    interface Controller{

        void getAddressInformation();

        String convertTime(int timeInMinutes);

        void changeAddressType();

        void changeOpeningHours(int hourOfDay, int minute, String workingHours);

        void googleLinkClick();

        void addCommentButtonClick();

        void eventReceived(Event event);
    }

    interface Model{

        void getAddressInformation(Address address, DatabaseCallback.AddressInformationCallBack callback);

        void changeAddressType(String username, Address address, DatabaseCallback.AddressTypeChangeCallback callback);

        void changeOpeningTime(Address address, int openingTime);

        void changeClosingTime(Address address, int closingTime);
    }

}
