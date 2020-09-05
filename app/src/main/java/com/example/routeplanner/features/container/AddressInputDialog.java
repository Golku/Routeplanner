package com.example.routeplanner.features.container;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.routeplanner.R;

public class AddressInputDialog extends AppCompatDialogFragment {

    private final String debugTag = "debugTag";

    private AddressInputDialogCallback callback;

    public interface AddressInputDialogCallback{
        void addAddress(String address);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.address_input_dialog, null);

        EditText streetInput = view.findViewById(R.id.street_input);
        EditText postcodeNumbersInput = view.findViewById(R.id.postcode_numbers_input);
        EditText postcodeLettersInput = view.findViewById(R.id.postcode_letters_input);
        EditText cityInput = view.findViewById(R.id.city_input);

        Button addBtn = view.findViewById(R.id.addAddressBtn);
        Button cancelBtn = view.findViewById(R.id.cancelAddressInputBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.addAddress(streetInput.getText().toString()+", "+
                postcodeNumbersInput.getText().toString()+" "+
                postcodeLettersInput.getText().toString()+" "+
                cityInput.getText().toString()+", Netherlands");
                dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        streetInput.requestFocus();
        streetInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        builder.setView(view);
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
