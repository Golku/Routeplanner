package com.example.routeplanner.data.models;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.example.routeplanner.features.container.addressDetailsFragment.AddressDetailsFragment;

import java.util.Calendar;

@SuppressLint("ValidFragment")
public class DialogCreator extends DialogFragment {

    AddressDetailsFragment addressDetailsFragment;

    public DialogCreator(AddressDetailsFragment addressDetailsFragment) {
        this.addressDetailsFragment = addressDetailsFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(
                getActivity(),
                addressDetailsFragment,
                hour,
                minute,
                true);
    }
}
