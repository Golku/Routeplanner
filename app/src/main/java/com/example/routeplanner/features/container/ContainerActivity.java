package com.example.routeplanner.features.container;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.routeplanner.R;
import com.example.routeplanner.data.models.GenericDialog;
import com.example.routeplanner.data.models.LoadingDialog;
import com.example.routeplanner.data.models.OptimisingDialog;
import com.example.routeplanner.data.models.RetryDialog;
import com.example.routeplanner.data.models.Utils;
import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.DialogMessage;
import com.example.routeplanner.data.pojos.Event;
import com.example.routeplanner.data.pojos.RouteInfoHolder;
import com.example.routeplanner.data.pojos.Session;
import com.example.routeplanner.data.pojos.api.Drive;
import com.example.routeplanner.features.container.addressListFragment.AddressListFragment;
import com.example.routeplanner.features.container.driveListFragment.DriveListFragment;
import com.example.routeplanner.features.container.mapFragment.MapFragment;
import com.example.routeplanner.features.login.LoginActivity;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContainerActivity extends AppCompatActivity implements MvcContainer.View,
        PredictionsAdapter.AdapterCallback,
        RetryDialog.retryDialogCallback,
        AddressInputDialog.AddressInputDialogCallback{

    @BindView(R.id.container)
    ConstraintLayout container;
    @BindView(R.id.action_menu_btn)
    ImageView actionMenuBtn;
    @BindView(R.id.add_stops_iv)
    ImageView add_stops_iv;
    @BindView(R.id.topBarWrapper)
    ConstraintLayout topBarWrapper;
    @BindView(R.id.loaderWrapper)
    ConstraintLayout loaderWrapper;
    @BindView(R.id.inputAddressComLo)
    ConstraintLayout inputAddressComLo;
    @BindView(R.id.subLayout)
    ConstraintLayout subLayout;
    @BindView(R.id.travel_info_wrapper)
    ConstraintLayout travel_info_wrapper;
    @BindView(R.id.company_icon_wrapper)
    ConstraintLayout company_icon_wrapper;
    @BindView(R.id.house_icon_wrapper)
    ConstraintLayout house_icon_wrapper;
    @BindView(R.id.end_time_wrapper)
    ConstraintLayout end_time_wrapper;
    @BindView(R.id.addressDetailsWrapper)
    ConstraintLayout addressDetailsWrapper;
    @BindView(R.id.nav_bar)
    BottomNavigationView navBar;
    @BindView(R.id.snack_bar_container)
    CoordinatorLayout snackBarContainer;
    @BindView(R.id.fragment_container)
    ViewPager fragmentContainer;
    @BindView(R.id.route_end_time)
    TextView routeEndTime;
    @BindView(R.id.routeEndTimeDiff_tv)
    TextView routeEndTimeDiffTv;
    @BindView(R.id.info_bar_wrapper)
    ConstraintLayout info_bar_wrapper;
    @BindView(R.id.input_wrapper)
    ConstraintLayout input_wrapper;
    @BindView(R.id.inputText)
    EditText inputText;
    @BindView(R.id.predictionsList)
    RecyclerView predictionsList;
    @BindView(R.id.loadingMessage)
    TextView loadingMessage;
    @BindView(R.id.totalStopsNumber)
    TextView totalStopsNumber;
    @BindView(R.id.privateCountTv)
    TextView privateCountTv;
    @BindView(R.id.businessCountTv)
    TextView businessCountTv;
    @BindView(R.id.routeDistanceTv)
    TextView routeDistanceTv;
    @BindView(R.id.routeDurationTv)
    TextView routeDurationTv;
    @BindView(R.id.primaryAddressInfo)
    TextView streetTextView;
    @BindView(R.id.secondaryAddressInfo)
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
    @BindView(R.id.opening_time_holder)
    TextView openingHoursHolder;
    @BindView(R.id.closing_time_holder)
    TextView closingHoursHolder;
    @BindView(R.id.packageCount_Tv)
    TextView packageCount_Tv;

    private ContainerController controller;

    private final String debugTag = "debugTag";

    private boolean containerLoaded;
    private boolean inputting;
    private boolean showingDetails;
    private boolean backPress;
    private boolean typing;
    private boolean addingAddress;
    private LoadingDialog loadingDialog;
    private OptimisingDialog optimisingDialog;

    @Override
    public void onBackPressed() {

        if(containerLoaded){
            fragmentContainer.setVisibility(View.VISIBLE);
            navBar.setVisibility(View.VISIBLE);
            input_wrapper.setVisibility(View.GONE);
            info_bar_wrapper.setVisibility(View.VISIBLE);
            predictionsList.setVisibility(View.GONE);
        }

        add_stops_iv.setVisibility(View.GONE);
        inputText.getText().clear();
        subLayout.setVisibility(View.GONE);
        showManualInputOption(false);

        if(showingDetails){
            hideAddressDetails();
            return;
        }

        if (backPress) {
            closeActivity();
        } else {
            if(inputting){
                inputting = false;
                return;
            }
            backPress = true;
            onBackPressToast();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(showingDetails){
            controller.updateCommentsList();
        }
    }

    private void onBackPressToast() {
        Snackbar snackbar = Snackbar.make(snackBarContainer, "Press again to exit", Snackbar.LENGTH_SHORT);
        snackbar.addCallback(new Snackbar.Callback(){
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                backPress = false;
            }
        });
        snackbar.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_container);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        Utils.darkenStatusBar(this, R.color.blue);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));

        this.loadingDialog = new LoadingDialog();
        this.optimisingDialog = new OptimisingDialog();
        loadingDialog.show(getSupportFragmentManager(), "Loading dialog");

        controller = new ContainerController(this, this, new Session(this));

        containerLoaded = false;
        inputting = false;
        showingDetails = false;
        backPress = false;
        typing = false;

        setupPredictionAdapter(new ArrayList<>());

        navBar.setOnNavigationItemSelectedListener(item -> {

            hideAddressDetails();
            hideAddressInputField();
            hideLoader();

            inputAddressComLo.setVisibility(View.GONE);
            add_stops_iv.setVisibility(View.GONE);

            switch(item.getItemId()){

                case R.id.nav_address:
                    fragmentContainer.setCurrentItem(0);
                    return true;
                case R.id.nav_map:
                    fragmentContainer.setCurrentItem(1);
                    return true;
                case R.id.nav_route:
                    fragmentContainer.setCurrentItem(2);
                    return true;
                default:
                    return false;
            }
        });

        inputText.addTextChangedListener(new TextWatcher() {

            // the user's changes are saved here
            public void onTextChanged(CharSequence c, int start, int before, int count) {

                if(addingAddress){
                    return;
                }

                if(typing){
                    hideAddressDetails();
                    predictionsList.setVisibility(View.VISIBLE);
                    typing = false;
                }

                if(inputText.getText().toString().length() >= 3){
                    add_stops_iv.setVisibility(View.GONE);
                    controller.getPrediction(inputText.getText().toString());
                }else if(inputText.getText().toString().length() <= 3){
                    add_stops_iv.setVisibility(View.VISIBLE);
                    setupPredictionAdapter(new ArrayList<AutocompletePrediction>());
                    showManualInputOption(false);
                }

            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
            }
        });

        controller.getContainer();
    }

    @Override
    public void showLoader(String message) {
        loadingMessage.setText(message);
        loaderWrapper.setVisibility(View.VISIBLE);
        loaderWrapper.bringToFront();
    }

    @Override
    public void hideLoader() {
        loaderWrapper.setVisibility(View.GONE);
        addingAddress = false;
        if(!containerLoaded){
            RetryDialog retryDialog = new RetryDialog();
            retryDialog.show(getSupportFragmentManager(), "Retry dialog");
        }
    }

    @Override
    public void getContainer() {
        loadingDialog.show(getSupportFragmentManager(), "Loading dialog");
        controller.getContainer();
    }

    @Override
    public void backToLogInScreen() {
        logOut();
    }

    @Override
    public void setupFragments(RouteInfoHolder routeInfoHolder) {

        Bundle bundle = new Bundle();
        bundle.putParcelable("routeInfoHolder", routeInfoHolder);

        Fragment addressListFragment = new AddressListFragment();
        Fragment MapFragment = new MapFragment();
        Fragment routeListFragment = new DriveListFragment();

        addressListFragment.setArguments(bundle);
        MapFragment.setArguments(bundle);
        routeListFragment.setArguments(bundle);

        ContainerSectionPagerAdapter containerSectionPagerAdapter = new ContainerSectionPagerAdapter(getSupportFragmentManager());
        containerSectionPagerAdapter.addFragment(addressListFragment);
        containerSectionPagerAdapter.addFragment(MapFragment);
        containerSectionPagerAdapter.addFragment(routeListFragment);

        fragmentContainer.setAdapter(containerSectionPagerAdapter);

        topBarWrapper.setVisibility(View.VISIBLE);
        info_bar_wrapper.setVisibility(View.VISIBLE);
        fragmentContainer.setVisibility(View.VISIBLE);
        navBar.setVisibility(View.VISIBLE);

        containerLoaded = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismiss();
            }
        },750);
    }

    @Override
    public void setupPredictionAdapter(List<AutocompletePrediction> predictions){
        PredictionsAdapter adapter = new PredictionsAdapter(this, predictions);
        predictionsList.setAdapter(adapter);
    }

    @Override
    public void showFragment(int position) {
        navBar.setSelectedItemId(R.id.nav_map);
        fragmentContainer.setCurrentItem(position);
    }

    @Override
    public void showManualInputOption(boolean show) {
        if (show) {
            inputAddressComLo.setVisibility(View.VISIBLE);
        } else {
            inputAddressComLo.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.openAddressInputDialogBtn)
    public void manualAddressInput(){
        AddressInputDialog addressInputDialog = new AddressInputDialog();
        addressInputDialog.show(getSupportFragmentManager(), "Address dialog");
    }

    @Override
    public void addAddress(String address) {
        showManualInputOption(false);
        inputText.getText().clear();
        predictionsList.setVisibility(View.GONE);
        add_stops_iv.setVisibility(View.GONE);

        showLoader("Adding address, please wait...");

        typing = true;
        addingAddress = true;
        controller.getAddress(address);
    }

    @Override
    public void showInputField() {

        inputting = true;
        inputText.getText().clear();
        fragmentContainer.setVisibility(View.GONE);
        info_bar_wrapper.setVisibility(View.GONE);
        input_wrapper.setVisibility(View.VISIBLE);
        predictionsList.setVisibility(View.VISIBLE);
        add_stops_iv.setVisibility(View.VISIBLE);
        add_stops_iv.bringToFront();

        inputText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(inputText, InputMethodManager.SHOW_IMPLICIT);
        }
        inputText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    @Override
    public void updateAddressCount(int privateAddress, int businessAddress) {

        totalStopsNumber.setText(String.valueOf(privateAddress+businessAddress)+" stops");

        if(privateAddress > 0){
            privateCountTv.setText(String.valueOf(privateAddress));
            house_icon_wrapper.setVisibility(View.VISIBLE);
        }else{
            house_icon_wrapper.setVisibility(View.GONE);
        }

        if(businessAddress > 0){
            businessCountTv.setText(String.valueOf(businessAddress));
            company_icon_wrapper.setVisibility(View.VISIBLE);
        }else{
            company_icon_wrapper.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateRouteTravelInfo(String totalRouteDistance, String totalRouteDuration, String endTime, String endTimeDifference, String color, boolean endTimeDiff) {

        routeDistanceTv.setText(totalRouteDistance);
        routeDurationTv.setText(totalRouteDuration);
        routeEndTime.setText(endTime);

        if(endTimeDiff){
            routeEndTimeDiffTv.setText(endTimeDifference);

            if(color.equals("red")){
                routeEndTimeDiffTv.setTextColor(ContextCompat.getColor(this, R.color.redStop));
            }else{
                routeEndTimeDiffTv.setTextColor(ContextCompat.getColor(this, R.color.niceGreen));
            }

            routeEndTimeDiffTv.setVisibility(View.VISIBLE);
        }else{
            routeEndTimeDiffTv.setVisibility(View.GONE);
        }


        if(!color.isEmpty()){
            travel_info_wrapper.setVisibility(View.VISIBLE);
            end_time_wrapper.setVisibility(View.VISIBLE);
        }else{
            travel_info_wrapper.setVisibility(View.GONE);
            end_time_wrapper.setVisibility(View.GONE);
        }

    }

    @Override
    public void predictionClick(String address) {
        inputText.setText(address+" ");
        inputText.setSelection(inputText.getText().length());
        controller.getPrediction(address);
    }

    @Override
    public void predictionSelected(String address) {
        inputText.getText().clear();
        predictionsList.setVisibility(View.GONE);
        add_stops_iv.setVisibility(View.GONE);

        showLoader("Adding address, please wait...");

        typing = true;
        addingAddress = true;
        controller.getAddress(address);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveEvent(Event event) {
        controller.eventReceived(event);
    }

    @Override
    public void postEvent(Event event) {
        EventBus.getDefault().post(event);
    }

    @Override
    public void showAddressDetails() {
        info_bar_wrapper.setVisibility(View.GONE);
        addressDetailsWrapper.setVisibility(View.VISIBLE);
        addressDetailsWrapper.bringToFront();
        showingDetails = true;
    }

    @OnClick(R.id.subLayout)
    public void onNewAddressClick(){
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(inputText.getWindowToken(), 0);
        subLayout.setVisibility(View.GONE);
        add_stops_iv.setVisibility(View.VISIBLE);
        controller.showAddressDetails();
    }

    @Override
    public void showNewAddressDetails(Address address) {
        if(address.isBusiness()){
            addressTypeImageView.setImageResource(R.drawable.company);
            openingTimeTv.setText(controller.convertTime(address.getOpeningTime()));
            closingTimeTv.setText(controller.convertTime(address.getClosingTime()));
            openingHoursHolder.setVisibility(View.VISIBLE);
            closingHoursHolder.setVisibility(View.VISIBLE);
            openingTimeTv.setVisibility(View.VISIBLE);
            closingTimeTv.setVisibility(View.VISIBLE);
        }else{
            addressTypeImageView.setImageResource(R.drawable.house);
            openingHoursHolder.setVisibility(View.GONE);
            closingHoursHolder.setVisibility(View.GONE);
            openingTimeTv.setVisibility(View.GONE);
            closingTimeTv.setVisibility(View.GONE);
        }
        streetTextView.setText(address.getStreet());
        if(address.getPostCode().isEmpty()){
            cityTextView.setText(address.getCity());
        }else{
            cityTextView.setText(address.getPostCode() + " " + address.getCity());
        }
        packageCount_Tv.setText(String.valueOf(address.getPackageCount())+" x");
        subLayout.setVisibility(View.VISIBLE);
        subLayout.bringToFront();
    }

    public void hideAddressDetails(){
        addressDetailsWrapper.setVisibility(View.GONE);
        subLayout.setVisibility(View.GONE);
        if(!typing || !inputting){
            info_bar_wrapper.setVisibility(View.VISIBLE);
        }
        showingDetails = false;
    }

    private void hideAddressInputField(){
        fragmentContainer.setVisibility(View.VISIBLE);
        input_wrapper.setVisibility(View.GONE);
        info_bar_wrapper.setVisibility(View.VISIBLE);
        predictionsList.setVisibility(View.GONE);
        typing = false;
    }

    @Override
    public void navigateToDestination(Drive drive) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + drive.getDestinationAddress().getLat() +", "+ drive.getDestinationAddress().getLng());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    @Override
    public void showLoginScreen() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    @Override
    public void showOptimisingDialog(boolean show) {
        if(show){
            optimisingDialog.show(getSupportFragmentManager(), "Loading dialog");
        }else{
            optimisingDialog.dismiss();
        }
    }

    @Override
    public void showDialog(String message) {
        DialogMessage dialogMessage = new DialogMessage(message);
        Bundle bundle = new Bundle();
        bundle.putParcelable("message", dialogMessage);
        GenericDialog dialog = new GenericDialog();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "Generic dialog");
    }

    @Override
    public void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @OnClick(R.id.action_menu_btn)
    public void logOut(){
        controller.logOut();
    }

    @Override
    public void closeActivity() {
        EventBus.getDefault().unregister(this);
        finish();
    }
}