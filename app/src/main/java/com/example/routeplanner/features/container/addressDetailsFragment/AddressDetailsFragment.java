package com.example.routeplanner.features.container.addressDetailsFragment;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddressDetailsFragment extends Fragment implements
        MvcAddressDetails.View,
        TimePickerDialog.OnTimeSetListener{

    @BindView(R.id.addressCommentsList)
    RecyclerView recyclerView;
    @BindView(R.id.removeStopBtn)
    ConstraintLayout removeStopBtn;
    @BindView(R.id.subLayout)
    ConstraintLayout subLayout;
    @BindView(R.id.workingHours_layout)
    ConstraintLayout workingHours_layout;
    @BindView(R.id.searchAddress)
    ConstraintLayout showOnMapLayout;
    @BindView(R.id.packageCount_layout)
    ConstraintLayout packageCount_layout;
    @BindView(R.id.action_bar)
    ConstraintLayout action_bar;
    @BindView(R.id.primaryAddressInfo)
    TextView primaryAddressInfo;
    @BindView(R.id.secondaryAddressInfo)
    TextView secondaryAddressInfo;
    @BindView(R.id.thirdAddressInfo)
    TextView thirdAddressInfo;
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
    @BindView(R.id.packageCount_Tv)
    TextView packageCount_Tv;
    @BindView(R.id.monday)
    TextView monday;
    @BindView(R.id.tuesday)
    TextView tuesday;
    @BindView(R.id.wednesday)
    TextView wednesday;
    @BindView(R.id.thursday)
    TextView thursday;
    @BindView(R.id.friday)
    TextView friday;
    @BindView(R.id.saturday)
    TextView saturday;
    @BindView(R.id.sunday)
    TextView sunday;


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

        action_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        subLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        workingHours_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        packageCount_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        showOnMapLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public void updateAddressInfo(Address address, boolean newAddress) {
        recyclerView.setAdapter(null);

        if(address.isBusiness()){
            addressTypeImageView.setImageResource(R.drawable.company);
            primaryAddressInfo.setText(address.getBusinessName());
            secondaryAddressInfo.setText(address.getStreet());
            secondaryAddressInfo.setTextColor(ContextCompat.getColor(getActivity(), R.color.streetDark));
            thirdAddressInfo.setVisibility(View.VISIBLE);
            if(address.getPostCode().isEmpty()){
                thirdAddressInfo.setText(address.getCity());
            }else{
                thirdAddressInfo.setText(address.getPostCode() + " " + address.getCity());
            }

            workingHours_layout.setVisibility(View.GONE);

            if(address.getWeekdayText().length > 0){
                monday.setText(address.getWeekdayText()[0]);
                tuesday.setText(address.getWeekdayText()[1]);
                wednesday.setText(address.getWeekdayText()[2]);
                thursday.setText(address.getWeekdayText()[3]);
                friday.setText(address.getWeekdayText()[4]);
                saturday.setText(address.getWeekdayText()[5]);
                sunday.setText(address.getWeekdayText()[6]);
                workingHours_layout.setVisibility(View.VISIBLE);
            }

        }else{
            addressTypeImageView.setImageResource(R.drawable.house);
            workingHours_layout.setVisibility(View.GONE);
            thirdAddressInfo.setVisibility(View.GONE);

            secondaryAddressInfo.setTextColor(ContextCompat.getColor(getActivity(), R.color.greyDot));

            primaryAddressInfo.setText(address.getStreet());
            if(address.getPostCode().isEmpty()){
                secondaryAddressInfo.setText(address.getCity());
            }else{
                secondaryAddressInfo.setText(address.getPostCode() + " " + address.getCity());
            }

        }

        packageCount_Tv.setText(String.valueOf(address.getPackageCount()));

//        changeAddressType(address);
        controller.getAddressInformation();
    }

    @Override
    public void setUpAdapter(AddressDetailsAdapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    @OnClick(R.id.back_arrow_btn)
    public void backArrow(){
        workingHours_layout.setVisibility(View.GONE);
        controller.hideAddressDetails();
    }

    //@OnClick(R.id.addressTypeImageView)
    public void typeChangeRequest() {
        addressTypeImageView.setVisibility(View.INVISIBLE);
        typeChangeProgress_pb.setVisibility(View.VISIBLE);
        controller.changeAddressType();
    }

    @OnClick(R.id.minusBtn)
    public void subFromPackageCount(){
        controller.updatePackageCount(-1);
    }

    @OnClick(R.id.addBtn)
    public void addToPackageCount(){
        controller.updatePackageCount(1);
    }

    @Override
    public void changePackageCountTextView(String count) {
        packageCount_Tv.setText(count);
    }

    @Override
    public void changeAddressType(Address address) {

        if(address.isBusiness()){
            addressTypeImageView.setImageResource(R.drawable.company);
//            openingTimeTv.setText(controller.convertTime(address.getOpeningTime()));
//            closingTimeTv.setText(controller.convertTime(address.getClosingTime()));
        }else{
            addressTypeImageView.setImageResource(R.drawable.house);
        }
    }

    @OnClick(R.id.showOnMapBtn)
    public void onShowOnMapBtnClick(){
        controller.hideAddressDetails();
        controller.showOnMap();
    }

    @OnClick(R.id.googleSearchBtn)
    public void onGoogleLinkClick() {
        controller.googleLinkClick();
    }

//    @OnClick(R.id.change_opening_time_tv)
    public void onChangeOpeningHoursClick(){

        if(((MyApplication) getActivity().getApplication()).isOrganizing()){
            showDialog("Can't change time while organising");
            return;
        }

        workingHours = "open";
        DialogFragment timePicker = new DialogCreator(this);
        timePicker.show(getActivity().getSupportFragmentManager(), "Time Picker");
    }

//    @OnClick(R.id.change_closing_time_tv)
    public void onOChangeClosingHoursClick(){

        if(((MyApplication) getActivity().getApplication()).isOrganizing()){
            showDialog("Can't change time while organising");
            return;
        }

        workingHours = "close";
        DialogFragment timePicker = new DialogCreator(this);
        timePicker.show(getActivity().getSupportFragmentManager(), "Time Picker");
    }

    @OnClick(R.id.removeStopBtn)
    public void onRemoveStopBtnClick(){
        controller.removeStop();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        int timeInMinutes = ((hourOfDay*60)+minute);

        String timeString = controller.convertTime(timeInMinutes);

//        switch (workingHours){
//            case "open" : openingTimeTv.setText(timeString);
//                break;
//            case "close" : closingTimeTv.setText(timeString);
//        }
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
        String url = "";
        if(address.isBusiness()){
            url = "http://www.google.com/search?q=" + address.getBusinessName()+ " " +address.getStreet() + " " + address.getCity();
        }else{
            url = "http://www.google.com/search?q=" + address.getStreet() + " " + address.getCity();
        }
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
