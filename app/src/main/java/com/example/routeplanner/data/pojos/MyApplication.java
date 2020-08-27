package com.example.routeplanner.data.pojos;

import android.app.Application;

public class MyApplication extends Application {

    private boolean isOrganizing;

    public boolean isOrganizing() {
        return isOrganizing;
    }

    public void setOrganizing(boolean organizing) {
        isOrganizing = organizing;
    }
}
