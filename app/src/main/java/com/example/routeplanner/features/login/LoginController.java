package com.example.routeplanner.features.login;

import android.util.Log;

import com.example.routeplanner.data.database.DatabaseCallback;
import com.example.routeplanner.data.database.DatabaseService;
import com.example.routeplanner.data.pojos.database.LoginResponse;
import com.example.routeplanner.features.shared.BaseController;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginController extends BaseController implements MvcLogin.Controller, DatabaseCallback.LoginCallBack{

    private final String debugTag = "debugTag";

    private MvcLogin.View view;
    private LoginModel model;

    private String username;

    LoginController(MvcLogin.View view) {
        this.view = view;
        this.model = new LoginModel(createDatabaseService());
    }

    @Override
    public void loginBtnClick(String username, String password) {

        this.username = username;

        //incription
        String encryptedUsername = encryptInput(username);
        String encryptedPassword = encryptInput(password);

        model.loginRequest(encryptedUsername, encryptedPassword, this);
    }

    private String encryptInput (String input){
        return input;
    }

    @Override
    public void onLoginResponse(LoginResponse response) {
        if (response == null) {
            view.showDialog("Something went wrong, please try again");
            return;
        }

        if(!response.isError()){
            beginSession(response.getUserId(), username, view.getSession());
            view.showContainer();
            view.closeActivity();
        }else{
            view.finishNetworkOperation();
            view.showDialog(response.getMessage());
//            view.showToast(response.getMessage());
        }
    }

    @Override
    public void onLoginResponseFailure() {
        view.finishNetworkOperation();
        view.showDialog("Failed to login, please try again");
//        view.showToast("Failed to login, please try again");
    }
}
