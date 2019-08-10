package com.example.routeplanner.features.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.routeplanner.R;
import com.example.routeplanner.data.models.Utils;
import com.example.routeplanner.data.pojos.Session;
import com.example.routeplanner.features.container.ContainerActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements MvcLogin.View{

    private final String debugTag = "debugTag";

    @BindView(R.id.username_input)
    EditText usernameInput;
    @BindView(R.id.password_input)
    EditText passwordInput;
    @BindView(R.id.login_btn)
    CardView loginBtn;

    private LoginController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        Utils.darkenStatusBar(this, R.color.colorBlack);
        controller = new LoginController(this);
    }

    @Override
    public Session getSession() {
        return new Session(this);
    }

    @OnClick(R.id.login_btn)
    @Override
    public void onLoginBtnClick() {
        loginBtn.setEnabled(false);
        String username = usernameInput.getText().toString().toLowerCase();
        String password = passwordInput.getText().toString();
        controller.loginBtnClick(username, password);
    }

    @Override
    public void showContainer() {
        Intent i = new Intent (this, ContainerActivity.class);
        startActivity(i);
    }

    @Override
    public void finishNetworkOperation() {
        loginBtn.setEnabled(true);
    }

    @Override
    public void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void closeActivity() {
        finish();
    }
}
