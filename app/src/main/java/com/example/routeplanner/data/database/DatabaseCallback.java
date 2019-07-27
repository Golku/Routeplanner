package com.example.routeplanner.data.database;

import com.example.routeplanner.data.pojos.database.AddressInformationResponse;
import com.example.routeplanner.data.pojos.database.AddressTypeResponse;
import com.example.routeplanner.data.pojos.database.CommentInputResponse;
import com.example.routeplanner.data.pojos.database.LoginResponse;

public interface DatabaseCallback {

    interface LoginCallBack{
        void onLoginResponse(LoginResponse response);
        void onLoginResponseFailure();
    }

    interface AddressInformationCallBack{
        void onAddressInformationResponse(AddressInformationResponse response);
        void onAddressInformationResponseFailure();
    }

    interface AddressTypeChangeCallback{
        void typeChangeResponse(AddressTypeResponse response);
        void typeChangeResponseFailure();
    }

    interface CommentInputCallBack{
        void onCommentInputResponse(CommentInputResponse response);
        void onCommentInputResponseFailure();
    }
}
