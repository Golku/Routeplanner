package com.example.routeplanner.data.models;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.routeplanner.R;
import com.example.routeplanner.data.pojos.DialogMessage;

import butterknife.ButterKnife;

public class GenericDialog extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        DialogMessage message = getArguments().getParcelable("message");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.generic_dialog_layout, null);

        TextView messageTv = view.findViewById(R.id.messageTv);
        messageTv.setText(message.getMessage());

        Button okBtn = view.findViewById(R.id.okBtn);
        Button cancelBtn = view.findViewById(R.id.cancelBtn);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }
}