package com.example.routeplanner.features.splash;

import com.example.routeplanner.data.pojos.Session;

public interface MvcSplash {

    interface View{

        void showContainer();

        void showLogin();

        void closeActivity();
    }

    interface Controller{

        void redirectUser(Session session);
    }

}
