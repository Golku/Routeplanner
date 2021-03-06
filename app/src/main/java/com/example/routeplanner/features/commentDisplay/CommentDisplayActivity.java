package com.example.routeplanner.features.commentDisplay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.routeplanner.R;
import com.example.routeplanner.data.pojos.CommentInformation;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentDisplayActivity extends AppCompatActivity {

    @BindView(R.id.username_tv)
    TextView usernameTv;
    @BindView(R.id.date_tv)
    TextView dateTv;
    @BindView(R.id.comment_tv)
    TextView comment_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_display);
        ButterKnife.bind(this);
        init();
    }

    private void init(){
        CommentInformation commentInformation = getIntent().getParcelableExtra("commentInformation");
        usernameTv.setText(commentInformation.getEmployeeId());
        dateTv.setText(commentInformation.getDate());
        comment_tv.setText(commentInformation.getComment());
    }
}
