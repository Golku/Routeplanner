package com.example.routeplanner.features.commentInput;

import com.example.routeplanner.data.database.DatabaseCallback;
import com.example.routeplanner.data.database.DatabaseService;
import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.Session;
import com.example.routeplanner.data.pojos.database.CommentInputResponse;
import com.example.routeplanner.features.shared.BaseController;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommentInputController extends BaseController implements MvcCommentInput.Presenter, DatabaseCallback.CommentInputCallBack {

    private MvcCommentInput.View view;
    private CommentInputModel model;

    private Session session;
    private Address address;
    private String date;

    CommentInputController(MvcCommentInput.View view) {
        this.view = view;
        this.model = new CommentInputModel(createDatabaseService());
    }

    @Override
    public void setUpInfo(Session session, Address address){
        this.session = session;
        this.address = address;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        this.date = sdf.format(System.currentTimeMillis());
        view.updateTextViews(session.getUsername(), date);
    }

    @Override
    public void onAddCommentBtnClick(String comment) {
        view.onStartNetworkOperation();
        model.addCommentToAddress(address, session.getUserId(), comment, date, this);
    }

    @Override
    public void onCommentInputResponse(CommentInputResponse response) {
        view.onFinishNetworkOperation();

        if(!response.isError()){
            view.closeActivity();
        }else{
            view.showToast("Failed to add comment, please try again");
        }
    }

    @Override
    public void onCommentInputResponseFailure() {
        view.onFinishNetworkOperation();
        view.showToast("Failed to add comment, please try again");
    }
}