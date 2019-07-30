package com.example.routeplanner.features.container;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.routeplanner.R;
import com.example.routeplanner.data.pojos.Event;
import com.example.routeplanner.data.pojos.RouteInfoHolder;
import com.example.routeplanner.data.pojos.Session;
import com.example.routeplanner.features.container.addressListFragment.AddressListFragment;
import com.example.routeplanner.features.container.driveListFragment.DriveListFragment;
import com.example.routeplanner.features.container.mapFragment.MapFragment;
import com.example.routeplanner.features.login.LoginActivity;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.PlacesClient;

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

    @Nullable
    @BindView(R.id.loading_screen)
    ConstraintLayout loadingScreen;
    @BindView(R.id.container)
    ConstraintLayout container;
    @BindView(R.id.inputAddressComLo)
    ConstraintLayout inputAddressComLo;
    @BindView(R.id.topBlackScreen)
    ConstraintLayout topBlackScreen;
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
    @BindView(R.id.input_wrapper)
    ConstraintLayout input_wrapper;
    @BindView(R.id.inputText)
    EditText inputText;
    @BindView(R.id.predictionsList)
    RecyclerView predictionsList;

    private ContainerController controller;

    private final String debugTag = "debugTag";

    private boolean inputting;
    private boolean showingDetails;
    private boolean backPress;

    private boolean typing;

    @Override
    public void onBackPressed() {

        fragmentContainer.setVisibility(View.VISIBLE);
        navBar.setVisibility(View.VISIBLE);
        input_wrapper.setVisibility(View.GONE);
        predictionsList.setVisibility(View.GONE);

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

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        //loadingScreen.bringToFront();
        controller = new ContainerController(this, this);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyAycv4bRa_NI4gl7WwkgLGs4EDhn44G8DY");
        }


        PlacesClient placesClient = Places.createClient(this);

        controller.setVariables(new Session(this), this, placesClient);

        inputting = false;
        showingDetails = false;
        backPress = false;
        typing = false;

        setupPredictionAdapter(new ArrayList<AutocompletePrediction>());

        navBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                hideAddressDetails();

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
        //loadingScreen.setVisibility(View.GONE);
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


    public void logUserOut() {
        controller.logOut();
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

        typing = true;
        controller.getAddress(address);
    }

    @Override
    public void showInputField() {

        inputting = true;

        fragmentContainer.setVisibility(View.GONE);
        navBar.setVisibility(View.GONE);
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
    public void updateDeliveryCompletion(int[] deliveryCompletion) {
//        privateCompletion.setText(String.valueOf(deliveryCompletion[0]) + "/" + String.valueOf(deliveryCompletion[1]) + " delivered");
//        businessCompletion.setText(String.valueOf(deliveryCompletion[2]) + "/" + String.valueOf(deliveryCompletion[3]) + " delivered");
    }

    @Override
    public void updateRouteEndTimeTv(String endTime) {
        routeEndTime.setText(endTime);
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

        ViewGroup.LayoutParams params = addressDetailsWrapper.getLayoutParams();
        params.height = (int) (0 * Resources.getSystem().getDisplayMetrics().density);
        addressDetailsWrapper.setLayoutParams(params);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(container);
        constraintSet.connect(R.id.addressDetailsWrapper, ConstraintSet.TOP, R.id.topBarWrapper, ConstraintSet.BOTTOM,8);
        constraintSet.applyTo(container);

        addressDetailsWrapper.setVisibility(View.VISIBLE);
        addressDetailsWrapper.bringToFront();

        showingDetails = true;
    }

    @Override
    public void showBottomAddressDetails() {

        ViewGroup.LayoutParams params = addressDetailsWrapper.getLayoutParams();
        params.height = (int) (400 * Resources.getSystem().getDisplayMetrics().density);
        addressDetailsWrapper.setLayoutParams(params);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(container);
        constraintSet.clear(R.id.addressDetailsWrapper, ConstraintSet.TOP);
        constraintSet.applyTo(container);

        addressDetailsWrapper.setVisibility(View.VISIBLE);
        topBlackScreen.setVisibility(View.VISIBLE);

        topBlackScreen.bringToFront();
        addressDetailsWrapper.bringToFront();

        showingDetails = true;
    }

    @OnClick(R.id.topBlackScreen)
    public void hideAddressDetails(){
        topBlackScreen.setVisibility(View.GONE);
        addressDetailsWrapper.setVisibility(View.GONE);
        showingDetails = false;
    }

    @Override
    public void navigateToDestination(String address) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + address);
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