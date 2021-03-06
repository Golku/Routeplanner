package com.example.routeplanner.features.container.driveListFragment;

import com.example.routeplanner.data.pojos.Event;

public interface MvcDriveList {

    interface View{

        void setupAdapter(DriveListAdapter adapter);

        void postEvent(Event event);

        void scrollToItem(int position);

        void showToast(String message);
    }

    interface Controller{

        void showDriveList();

        void eventReceived(Event event);
    }

}
