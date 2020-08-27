package com.example.routeplanner.data.models;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.example.routeplanner.R;
import com.example.routeplanner.data.pojos.DialogMessage;

public class GenericDialog extends AppCompatDialogFragment {

    private TextView messageTv;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        DialogMessage message = getArguments().getParcelable("message");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.generic_dialog_layout, null);
        messageTv = view.findViewById(R.id.messageTv);
        messageTv.setText(message.getMessage());
        builder.setView(view)
                .setTitle("Message")
                .setPositiveButton("ok", (dialogInterface, i) -> {
                });
        return builder.create();
    }


}