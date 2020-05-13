package com.example.routeplanner.features.login;

import android.util.Log;

import com.example.routeplanner.data.database.DatabaseCallback;
import com.example.routeplanner.data.database.DatabaseService;
import com.example.routeplanner.data.pojos.database.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginModel implements MvcLogin.Model{

    private DatabaseService databaseService;
    private final String debugTag = "debugTag";

    LoginModel(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public void loginRequest(final String username,
                             final String password,
                             final DatabaseCallback.LoginCallBack callBack) {

        Call<LoginResponse> call = databaseService.login(username, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                callBack.onLoginResponse(response.body());
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callBack.onLoginResponseFailure();
            }
        });
    }
}
