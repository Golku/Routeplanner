package com.example.routeplanner.features.splash;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.routeplanner.R;
import com.example.routeplanner.data.pojos.Session;
import com.example.routeplanner.features.container.ContainerActivity;
import com.example.routeplanner.features.login.LoginActivity;

public class SplashActivity extends AppCompatActivity implements MvcSplash.View{

    private SplashController controller;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
    }

    private void init(){
        controller = new SplashController(this);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                controller.redirectUser(new Session(SplashActivity.this));
            }
        }, 2000);
    }

    @Override
    public void showLogin() {
        Intent i = new Intent (this, LoginActivity.class);
        startActivity(i);
    }

    @Override
    public void showContainer() {
        Intent i = new Intent (this, ContainerActivity.class);
        startActivity(i);
    }

    @Override
    public void closeActivity(){
        finish();
    }
}
