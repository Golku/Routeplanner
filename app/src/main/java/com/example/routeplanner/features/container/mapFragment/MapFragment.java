package com.example.routeplanner.features.container.mapFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.routeplanner.R;
import com.example.routeplanner.data.models.GenericDialog;
import com.example.routeplanner.data.pojos.DialogMessage;
import com.example.routeplanner.data.pojos.Event;
import com.example.routeplanner.data.pojos.MyApplication;
import com.example.routeplanner.data.pojos.RouteInfoHolder;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapFragment extends Fragment implements
        MvcMap.View,
        OnMapReadyCallback {

    @BindView(R.id.mapView)
    MapView mapView;

    @BindView(R.id.get_user_location_btn)
    FloatingActionButton getUserLocationBtn;

    @BindView(R.id.snack_bar_container)
    CoordinatorLayout snackBarContainer;

    @BindView(R.id.organizingRouteWrapper)
    ConstraintLayout organizingRouteWrapper;

    private final String debugTag = "debugTag";

    private MapController controller;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        RouteInfoHolder routeInfoHolder = getArguments().getParcelable("routeInfoHolder");

        controller = new MapController(this, routeInfoHolder.getAddressList(), routeInfoHolder.getDriveList());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapView mapView = view.findViewById(R.id.mapView);

        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        controller.setMapData(googleMap, this);
    }

    @OnClick(R.id.get_user_location_btn)
    public void updateUserLocation(){
        controller.getUserLocation();
    }

    @Override
    public boolean getOrganizing() {
        return ((MyApplication) getActivity().getApplication()).isOrganizing();
    }

    @Override
    public void deselectedMultipleMarkers() {
        Snackbar snackbar = Snackbar.make(snackBarContainer, "Deselect until here?", Snackbar.LENGTH_SHORT);
        snackbar.setAction("yes", v -> controller.multipleMarkersDeselected());
        snackbar.show();
    }

    @Override
    public void addPolylineToMap(List<LatLng> newDecodedPath, GoogleMap googleMap) {
        getActivity().runOnUiThread(() -> {
            Polyline polyline = googleMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
            polyline.setColor(ContextCompat.getColor(getActivity(), R.color.ice));
            controller.storePolyline(polyline);
        });
    }

    @Override
    public void removePolylineFromMap(Polyline polyline) {
        getActivity().runOnUiThread(polyline::remove);
    }

    @Override
    public void updatePolyline(Polyline polyline) {
        getActivity().runOnUiThread(() -> {
            polyline.setColor(ContextCompat.getColor(getActivity(), R.color.glacierBlue));
            polyline.setZIndex(1);
        });
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
    public void showOrganizingRouteLoader(boolean show) {
        if(show){
//            mapView.setVisibility(View.GONE);
            getUserLocationBtn.setClickable(false);
            organizingRouteWrapper.setVisibility(View.VISIBLE);
        }else{
            organizingRouteWrapper.setVisibility(View.GONE);
            getUserLocationBtn.setClickable(true);
//            mapView.setVisibility(View.VISIBLE);
        }
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
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}