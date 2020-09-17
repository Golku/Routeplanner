package com.example.routeplanner.features.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.routeplanner.R;
import com.example.routeplanner.data.models.GenericDialog;
import com.example.routeplanner.data.models.Utils;
import com.example.routeplanner.data.pojos.DialogMessage;
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
    @BindView(R.id.login_tv)
    TextView login_tv;
    @BindView(R.id.login_pb)
    ProgressBar login_pb;

    private LoginController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        Utils.darkenStatusBar(this, R.color.blueLight);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.whiteGrey));
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
        login_tv.setVisibility(View.GONE);
        login_pb.setVisibility(View.VISIBLE);
        String username = usernameInput.getText().toString().toLowerCase();
        String password = passwordInput.getText().toString();

        View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        controller.loginBtnClick(username, password);
//        showContainer();
//        closeActivity();
    }

    @Override
    public void showContainer() {
        Intent i = new Intent (this, ContainerActivity.class);
        startActivity(i);
    }

    @Override
    public void finishNetworkOperation() {
        login_tv.setVisibility(View.VISIBLE);
        login_pb.setVisibility(View.GONE);
        loginBtn.setEnabled(true);
    }

    @Override
    public void showDialog(String message) {
        DialogMessage dialogMessage = new DialogMessage(message);
        Bundle bundle = new Bundle();
        bundle.putParcelable("message", dialogMessage);
        GenericDialog dialog = new GenericDialog();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "Generic dialog");
    }

    @Override
    public void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void closeActivity() {
        finish();
    }
}
