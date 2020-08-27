package com.example.routeplanner.features.login;

import com.example.routeplanner.data.database.DatabaseCallback;
import com.example.routeplanner.data.pojos.Session;

public interface MvcLogin {

    interface View{

        Session getSession();

        void onLoginBtnClick();

        void showContainer();

        void showDialog(String message);

        void showToast(String message);

        void finishNetworkOperation();

        void closeActivity();
    }

    interface Controller{

        void loginBtnClick(String username, String password);

    }

    interface Model{

        void loginRequest(String username, String password, DatabaseCallback.LoginCallBack callBack);

    }

}