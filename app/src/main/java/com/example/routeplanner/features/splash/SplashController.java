package com.example.routeplanner.features.splash;

import com.example.routeplanner.data.pojos.Session;
import com.example.routeplanner.features.shared.BaseController;

public class SplashController extends BaseController implements MvcSplash.Controller{

    private MvcSplash.View view;

    SplashController(MvcSplash.View view) {
        this.view = view;
    }

    @Override
    public void redirectUser(Session session) {

        if(verifySession(session) && verifySessionTimeOut(session)){
            view.showContainer();
        }else{
            session.setActive(false);
            view.showLogin();
        }

        view.closeActivity();
    }
}
