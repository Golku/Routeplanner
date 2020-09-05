package com.example.routeplanner.data.models;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.example.routeplanner.R;

public class RetryDialog extends AppCompatDialogFragment {

    private retryDialogCallback callback;

    public interface retryDialogCallback{
        void getContainer();
        void backToLogInScreen();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.retry_dialog, null);

        Button retryBtn = view.findViewById(R.id.retryBtn);
        Button cancelBtn = view.findViewById(R.id.cancelRetryBtn);

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                callback.getContainer();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                callback.backToLogInScreen();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            callback = (retryDialogCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement callback");
        }
    }
}