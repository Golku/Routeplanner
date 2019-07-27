package com.example.routeplanner.features.commentInput;

import android.util.Log;

import com.example.routeplanner.data.database.DatabaseCallback;
import com.example.routeplanner.data.database.DatabaseService;
import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.database.CommentInputResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentInputModel implements MvcCommentInput.Model{

    private final String debugTag = "debugTag";

    private DatabaseService databaseService;

    CommentInputModel(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public void addCommentToAddress(Address address, int userId, String comment, String date, final DatabaseCallback.CommentInputCallBack callback) {

        Call<CommentInputResponse> call = databaseService.addCommentToAddress(
                address.getStreet(),
                address.getPostCode(),
                address.getCity(),
                userId,
                comment,
                date
        );

        call.enqueue(new Callback<CommentInputResponse>() {
            @Override
            public void onResponse(Call<CommentInputResponse> call, Response<CommentInputResponse> response) {
                callback.onCommentInputResponse(response.body());
            }

            @Override
            public void onFailure(Call<CommentInputResponse> call, Throwable t) {
                callback.onCommentInputResponseFailure();
            }
        });

    }
}
