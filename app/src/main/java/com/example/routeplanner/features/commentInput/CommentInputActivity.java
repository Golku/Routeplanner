package com.example.routeplanner.features.commentInput;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.routeplanner.R;
import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.Session;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommentInputActivity extends AppCompatActivity implements MvcCommentInput.View{

    @BindView(R.id.username_tv)
    TextView usernameTv;
    @BindView(R.id.date_tv)
    TextView dateTextView;
    @BindView(R.id.commentEditText)
    EditText commentEditText;
    @BindView(R.id.get_user_location_btn)
    Button addCommentBtn;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.messageToUserTextView)
    TextView messageToUserTextView;

    private CommentInputController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_input);
        ButterKnife.bind(this);
        init();
    }

    private void init(){
        controller = new CommentInputController(this);
        Address address = getIntent().getParcelableExtra("address");
        controller.setUpInfo(new Session(this), address);
    }

    @Override
    public void updateTextViews(String employeeId, String date) {
        usernameTv.setText(employeeId);
        dateTextView.setText(date);
    }

    @OnClick(R.id.get_user_location_btn)
    @Override
    public void onAddCommentBtnClick() {
        String comment = commentEditText.getText().toString();
        controller.onAddCommentBtnClick(comment);
    }

    @Override
    public void onStartNetworkOperation() {
        addCommentBtn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        messageToUserTextView.setVisibility(View.VISIBLE);
        messageToUserTextView.setText("Adding list_item_comment...");
    }

    @Override
    public void onFinishNetworkOperation() {
        addCommentBtn.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        messageToUserTextView.setVisibility(View.GONE);
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