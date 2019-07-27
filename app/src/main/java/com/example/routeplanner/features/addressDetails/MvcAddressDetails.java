package com.example.routeplanner.features.addressDetails;

import com.example.routeplanner.data.database.DatabaseCallback;
import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.CommentInformation;
import com.example.routeplanner.data.pojos.Session;
import com.example.routeplanner.data.pojos.database.AddressInformation;

public interface MvcAddressDetails {

    interface View{

        void setUpAdapter(AddressDetailsAdapter adapter);

        void updateMessageToUserTextView(String message);

        void changeAddressType(Address address);

        void networkOperationStarted();

        void networkOperationFinish();

        void showAddressInGoogle(Address address);

        void showCommentDisplay(CommentInformation commentInformation);

        void showCommentInput(Address address);

        void showToast(String message);

        void closeActivity();
    }

    interface Controller{

        void setInfo(Session session, Address address);

        void getAddressInformation();

        String convertTime(int timeInMinutes);

        void changeAddressType();

        void changeOpeningHours(int hourOfDay, int minute, String workingHours);

        void googleLinkClick();

        void addCommentButtonClick();
    }

    interface Model{

        void getAddressInformation(Address address, DatabaseCallback.AddressInformationCallBack callback);

        void changeAddressType(String username, Address address, DatabaseCallback.AddressTypeChangeCallback callback);

        void changeOpeningTime(Address address, int openingTime);

        void changeClosingTime(Address address, int closingTime);
    }

}
