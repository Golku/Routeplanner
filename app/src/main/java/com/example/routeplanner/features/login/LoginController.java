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

        Retrofit retrofit = new Retrofit.Builder()
                .client(new OkHttpClient())
                .baseUrl("http://212.187.39.139/map/v1/")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();

        this.model = new LoginModel(retrofit.create(DatabaseService.class));
    }

    @Override
    public void loginBtnClick(String username, String password) {

        this.username = username;

        //incription
        String encryptedUsername = encryptInput(username);
        String encryptedPassword = encryptInput(password);

        Log.d(debugTag, "Login");
        model.loginRequest(encryptedUsername, encryptedPassword, this);
    }

    private String encryptInput (String input){
        return input;
    }

    @Override
    public void onLoginResponse(LoginResponse response) {
        if (response == null) {
            view.showToast("Response is null");
            return;
        }

        if(response.isMatch()){
            beginSession(response.getUserId(), username, view.getSession());
            view.showContainer();
            view.closeActivity();
        }else{
            view.finishNetworkOperation();
            view.showToast("No match");
        }
    }

    @Override
    public void onLoginResponseFailure() {
        view.finishNetworkOperation();
        view.showToast("Failed to login");
    }
}
