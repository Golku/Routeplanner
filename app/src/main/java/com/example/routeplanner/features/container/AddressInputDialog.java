package com.example.routeplanner.features.container;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.routeplanner.R;

public class AddressInputDialog extends AppCompatDialogFragment {

    private final String debugTag = "debugTag";

    private EditText streetInput;
    private EditText postcodeLettersInput;
    private EditText postcodeNumbersInput;
    private EditText cityInput;

    private AddressInputDialogCallback callback;

    public interface AddressInputDialogCallback{
        void addAddress(String address);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.address_input_dialog, null);

        builder.setView(view)
                .setTitle("Add new address")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callback.addAddress(streetInput.getText().toString()+", "+
                                postcodeNumbersInput.getText().toString()+" "+
                                postcodeLettersInput.getText().toString()+" "+
                                cityInput.getText().toString()+", Netherlands");
                    }
                });

        streetInput = view.findViewById(R.id.street_input);
        postcodeNumbersInput = view.findViewById(R.id.postcode_numbers_input);
        postcodeLettersInput = view.findViewById(R.id.postcode_letters_input);
        cityInput = view.findViewById(R.id.city_input);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            callback = (AddressInputDialogCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement callback");
        }

    }
}
