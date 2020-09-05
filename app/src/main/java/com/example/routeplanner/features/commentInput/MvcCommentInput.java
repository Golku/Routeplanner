package com.example.routeplanner.features.commentInput;

import com.example.routeplanner.data.database.DatabaseCallback;
import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.Session;

public interface MvcCommentInput {

    interface View{

        void updateTextViews(String employeeId, String date);

        void onAddCommentBtnClick();

        void closeActivity();

        void onStartNetworkOperation();

        void onFinishNetworkOperation();

        void showDialog(String message);

        void showToast(String message);
    }

    interface Presenter{

        void setUpInfo(Session session, Address address);

        void onAddCommentBtnClick(String comment);
    }

    interface Model{
        void addCommentToAddress(
                Address address,
                int userId,
                String comment,
                String date,
                DatabaseCallback.CommentInputCallBack callback);
    }
}
