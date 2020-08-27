package com.example.routeplanner.features.container;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.routeplanner.R;
import com.example.routeplanner.data.models.GenericDialog;
import com.example.routeplanner.data.models.Utils;
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
        AddressInputDialog.AddressInputDialogCallback{

    @BindView(R.id.container)
    ConstraintLayout container;
    @BindView(R.id.topBarWrapper)
    ConstraintLayout topBarWrapper;
    @BindView(R.id.loaderWrapper)
    ConstraintLayout loaderWrapper;
    @BindView(R.id.retryWrapper)
    ConstraintLayout retryWrapper;
    @BindView(R.id.inputAddressComLo)
    ConstraintLayout inputAddressComLo;
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

    private ContainerController controller;

    private final String debugTag = "debugTag";

    private boolean containerLoaded;
    private boolean inputting;
    private boolean showingDetails;
    private boolean backPress;
    private boolean typing;

    @Override
    public void onBackPressed() {

        if(containerLoaded){
            fragmentContainer.setVisibility(View.VISIBLE);
            navBar.setVisibility(View.VISIBLE);
            input_wrapper.setVisibility(View.GONE);
            info_bar_wrapper.setVisibility(View.VISIBLE);
            predictionsList.setVisibility(View.GONE);
        }

        inputText.getText().clear();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.sortRoute:
                if(containerLoaded){
                    controller.getOrganizedRoute();
                }
                return true;
            case R.id.logOut:
                controller.logOut();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void init() {
        changeTopBarColor("colorDarkBlue");
        showLoader("Loading container, please wait...");

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

                if(typing){

                    hideAddressDetails();

                    predictionsList.setVisibility(View.VISIBLE);
                    typing = false;
                }

                if(inputText.getText().toString().length() >= 3){
//                    showLoader("Powered by Google");
                    controller.getPrediction(inputText.getText().toString());
                }else if(inputText.getText().toString().length() <= 3){
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
        if(!containerLoaded){
            retryWrapper.setVisibility(View.VISIBLE);
            retryWrapper.bringToFront();
        }
    }

    @OnClick(R.id.retryBtn)
    public void getContainer(){
        retryWrapper.setVisibility(View.GONE);
        showLoader("Loading container, please wait...");
        controller.getContainer();
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
        fragmentContainer.setVisibility(View.VISIBLE);
        navBar.setVisibility(View.VISIBLE);

        containerLoaded = true;
        hideLoader();
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
        addressInputDialog.show(getSupportFragmentManager(), "Add new address");
    }

    @Override
    public void addAddress(String address) {
        showManualInputOption(false);
        inputText.getText().clear();
        predictionsList.setVisibility(View.GONE);

        showLoader("Adding address, please wait...");

        typing = true;
        controller.getAddress(address);
    }

    @Override
    public void showInputField() {

        inputting = true;

        fragmentContainer.setVisibility(View.GONE);
        info_bar_wrapper.setVisibility(View.GONE);
        input_wrapper.setVisibility(View.VISIBLE);
        predictionsList.setVisibility(View.VISIBLE);

        inputText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.showSoftInput(inputText, InputMethodManager.SHOW_IMPLICIT);
        }

        inputText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    @Override
    public void updateAddressCount(int privateAddress, int businessAddress) {
        privateCountTv.setText(String.valueOf(privateAddress));
        businessCountTv.setText(String.valueOf(businessAddress));
        totalStopsNumber.setText(String.valueOf(privateAddress+businessAddress));
    }

    @Override
    public void updateRouteTravelInfo(String totalRouteDistance, String totalRouteDuration, String endTime, String endTimeDifference, String color, boolean endTimeDiff) {
        routeDistanceTv.setText(totalRouteDistance);
        routeDurationTv.setText(totalRouteDuration);
        routeEndTime.setText(endTime);

        if(endTimeDiff){
            routeEndTimeDiffTv.setText(endTimeDifference);

            if(color.equals("red")){
                routeEndTimeDiffTv.setTextColor(Color.RED);
            }else{
                routeEndTimeDiffTv.setTextColor(Color.GREEN);
            }

            routeEndTimeDiffTv.setVisibility(View.VISIBLE);
        }else{
            routeEndTimeDiffTv.setVisibility(View.GONE);
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

        showLoader("Adding address, please wait...");

        typing = true;
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
    public void showTopAddressDetails() {

        fragmentContainer.setVisibility(View.GONE);
        addressDetailsWrapper.setVisibility(View.VISIBLE);
        addressDetailsWrapper.bringToFront();
        showingDetails = true;
    }

    @Override
    public void showBottomAddressDetails() {

        fragmentContainer.setVisibility(View.GONE);
        addressDetailsWrapper.setVisibility(View.VISIBLE);
        addressDetailsWrapper.bringToFront();
        showingDetails = true;
    }

    public void hideAddressDetails(){
        addressDetailsWrapper.setVisibility(View.GONE);
        if(!typing){
            fragmentContainer.setVisibility(View.VISIBLE);
        }
        showingDetails = false;
    }

    private void hideAddressInputField(){
        fragmentContainer.setVisibility(View.VISIBLE);
        input_wrapper.setVisibility(View.GONE);
        info_bar_wrapper.setVisibility(View.VISIBLE);
        predictionsList.setVisibility(View.GONE);
    }

    private void changeTopBarColor(String color){
        switch (color){
            case "colorDarkBlue":
                Utils.darkenStatusBar(this, R.color.colorDarkBlue);
                break;
            case "colorBlack":
                Utils.darkenStatusBar(this, R.color.colorBlack);
                break;
        }
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

    @Override
    public void closeActivity() {
        EventBus.getDefault().unregister(this);
        finish();
    }
}