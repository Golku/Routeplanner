package com.example.routeplanner.features.container.addressDetailsFragment;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.routeplanner.R;
import com.example.routeplanner.data.models.DialogCreator;
import com.example.routeplanner.data.models.GenericDialog;
import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.CommentInformation;
import com.example.routeplanner.data.pojos.DialogMessage;
import com.example.routeplanner.data.pojos.Event;
import com.example.routeplanner.data.pojos.MyApplication;
import com.example.routeplanner.data.pojos.Session;
import com.example.routeplanner.features.commentDisplay.CommentDisplayActivity;
import com.example.routeplanner.features.commentInput.CommentInputActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.time.LocalDate;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddressDetailsFragment extends Fragment implements
        MvcAddressDetails.View,
        TimePickerDialog.OnTimeSetListener{

    @BindView(R.id.addressCommentsList)
    RecyclerView recyclerView;
    @BindView(R.id.streetTextView)
    TextView streetTextView;
    @BindView(R.id.postcodeTextView)
    TextView postcodeTextView;
    @BindView(R.id.cityTextView)
    TextView cityTextView;
    @BindView(R.id.addressTypeImageView)
    ImageView addressTypeImageView;
    @BindView(R.id.messageToUserTextView)
    TextView messageToUserTextView;
    @BindView(R.id.addCommentBtn)
    FloatingActionButton addCommentBtn;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.typeChangeProgress_pb)
    ProgressBar typeChangeProgress_pb;
    @BindView(R.id.opening_time_tv)
    TextView openingTimeTv;
    @BindView(R.id.closing_time_tv)
    TextView closingTimeTv;
    @BindView(R.id.change_opening_time_tv)
    TextView changeOpeningTimeTv;
    @BindView(R.id.change_closing_time_tv)
    TextView changeClosingTimeTv;
    @BindView(R.id.opening_time_holder)
    TextView openingHoursHolder;
    @BindView(R.id.closing_time_holder)
    TextView closingHoursHolder;
    @BindView(R.id.packageCount_Tv)
    TextView packageCount_Tv;

    private final String debugTag = "debugTag";

    private AddressDetailsController controller;

    private String workingHours;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        controller = new AddressDetailsController(this, new Session(getActivity()));
    }

    @Override
    public void updateAddressInfo(Address address, boolean newAddress) {
        recyclerView.setAdapter(null);

        streetTextView.setText(address.getStreet());
        postcodeTextView.setText(address.getPostCode());
        cityTextView.setText(address.getCity());
        packageCount_Tv.setText(String.valueOf(address.getPackageCount()));

        if (address.isBusiness()) {
            openingTimeTv.setVisibility(View.VISIBLE);
            changeOpeningTimeTv.setVisibility(View.VISIBLE);
            closingTimeTv.setVisibility(View.VISIBLE);
            changeClosingTimeTv.setVisibility(View.VISIBLE);
            openingHoursHolder.setVisibility(View.VISIBLE);
            closingHoursHolder.setVisibility(View.VISIBLE);
            openingTimeTv.setText(controller.convertTime(address.getOpeningTime()));
            closingTimeTv.setText(controller.convertTime(address.getClosingTime()));
            addressTypeImageView.setImageResource(R.drawable.business_ic_white);
        }else{
            openingTimeTv.setVisibility(View.GONE);
            changeOpeningTimeTv.setVisibility(View.GONE);
            closingTimeTv.setVisibility(View.GONE);
            changeClosingTimeTv.setVisibility(View.GONE);
            openingHoursHolder.setVisibility(View.GONE);
            closingHoursHolder.setVisibility(View.GONE);
            addressTypeImageView.setImageResource(R.drawable.home_ic_white);
        }

        controller.getAddressInformation();
    }

    @Override
    public void setUpAdapter(AddressDetailsAdapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    @OnClick(R.id.addressTypeImageView)
    public void typeChangeRequest() {
        addressTypeImageView.setVisibility(View.INVISIBLE);
        typeChangeProgress_pb.setVisibility(View.VISIBLE);
        controller.changeAddressType();

//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setMessage("Are you sure?")
//                .setTitle("Change address type")
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        addressTypeImageView.setVisibility(View.INVISIBLE);
//                        typeChangeProgress_pb.setVisibility(View.VISIBLE);
//                        controller.changeAddressType();
//                    }
//                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                // User cancelled the dialog
//            }
//        });
//        AlertDialog dialog = builder.create();
//        dialog.show();
    }

    @OnClick(R.id.packageCount_Tv)
    public void updatePackageCount(){
        controller.updatePackageCount();
    }

    @Override
    public void changePackageCountTextView(String count) {
        packageCount_Tv.setText(count);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void changeAddressType(Address address) {

        if(address.isBusiness()){
            addressTypeImageView.setImageResource(R.drawable.business_ic_white);
            openingTimeTv.setText(controller.convertTime(address.getOpeningTime()));
            closingTimeTv.setText(controller.convertTime(address.getClosingTime()));
            openingHoursHolder.setVisibility(View.VISIBLE);
            closingHoursHolder.setVisibility(View.VISIBLE);
            openingTimeTv.setVisibility(View.VISIBLE);
            changeOpeningTimeTv.setVisibility(View.VISIBLE);
            closingTimeTv.setVisibility(View.VISIBLE);
            changeClosingTimeTv.setVisibility(View.VISIBLE);
        }else{
            addressTypeImageView.setImageResource(R.drawable.home_ic_white);
            openingHoursHolder.setVisibility(View.GONE);
            closingHoursHolder.setVisibility(View.GONE);
            openingTimeTv.setVisibility(View.GONE);
            changeOpeningTimeTv.setVisibility(View.GONE);
            closingTimeTv.setVisibility(View.GONE);
            changeClosingTimeTv.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.googleSearchBtn)
    public void onGoogleLinkClick() {
        controller.googleLinkClick();
    }

    @OnClick(R.id.change_opening_time_tv)
    public void onChangeOpeningHoursClick(){

        if(((MyApplication) getActivity().getApplication()).isOrganizing()){
            showDialog("Can't change time while organising");
            return;
        }

        workingHours = "open";
        DialogFragment timePicker = new DialogCreator(this);
        timePicker.show(getActivity().getSupportFragmentManager(), "Time Picker");
    }

    @OnClick(R.id.change_closing_time_tv)
    public void onOChangeClosingHoursClick(){

        if(((MyApplication) getActivity().getApplication()).isOrganizing()){
            showDialog("Can't change time while organising");
            return;
        }

        workingHours = "close";
        DialogFragment timePicker = new DialogCreator(this);
        timePicker.show(getActivity().getSupportFragmentManager(), "Time Picker");
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        int timeInMinutes = ((hourOfDay*60)+minute);

        String timeString = controller.convertTime(timeInMinutes);

        switch (workingHours){
            case "open" : openingTimeTv.setText(timeString);
                break;
            case "close" : closingTimeTv.setText(timeString);
        }
        controller.changeOpeningHours(hourOfDay, minute, workingHours);
    }

    @OnClick(R.id.addCommentBtn)
    public void onAddCommentButtonClick() {
        controller.addCommentButtonClick();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveEvent(Event event){
        controller.eventReceived(event);
    }

    @Override
    public void postEvent(Event event) {
        EventBus.getDefault().post(event);
    }
    @Override
    public void showAddressInGoogle(Address address) {
        String url = "http://www.google.com/search?q=" + address.getStreet() + " " + address.getCity();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void networkOperationStarted(String message) {

        if(!message.isEmpty()){
            messageToUserTextView.setText(message);
        }else{
            messageToUserTextView.setVisibility(View.GONE);
        }

        progressBar.setVisibility(View.VISIBLE);
        messageToUserTextView.setVisibility(View.VISIBLE);
        //typeChangeProgress_pb.setVisibility(View.VISIBLE);
    }

    @Override
    public void networkOperationFinish(int operation, String message) {

        switch (operation) {
            case 1:
                messageToUserTextView.setText(message);
                progressBar.setVisibility(View.INVISIBLE);
                break;
            case 2:
                typeChangeProgress_pb.setVisibility(View.GONE);
                addressTypeImageView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void showCommentDisplay(CommentInformation commentInformation) {
        Intent i = new Intent(getActivity(), CommentDisplayActivity.class);
        i.putExtra("commentInformation", commentInformation);
        startActivity(i);
    }

    @Override
    public void showCommentInput(Address address) {
        Intent i = new Intent(getActivity(), CommentInputActivity.class);
        i.putExtra("address", address);
        startActivity(i);
    }

    @Override
    public void scrollToComment(int position) {
        recyclerView.smoothScrollToPosition(position);
    }

    @Override
    public void showDialog(String message) {
        DialogMessage dialogMessage = new DialogMessage(message);
        Bundle bundle = new Bundle();
        bundle.putParcelable("message", dialogMessage);
        GenericDialog dialog = new GenericDialog();
        dialog.setArguments(bundle);
        dialog.show(getActivity().getSupportFragmentManager(), "Generic dialog");
    }

    @Override
    public void showToast(String message) {
        Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
