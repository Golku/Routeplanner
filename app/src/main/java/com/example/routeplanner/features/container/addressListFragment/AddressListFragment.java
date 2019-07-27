package com.example.routeplanner.features.container.addressListFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.routeplanner.R;
import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.Event;
import com.example.routeplanner.data.pojos.RouteInfoHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddressListFragment extends Fragment implements MvcAddressList.View{

    @BindView(R.id.address_list)
    RecyclerView recyclerView;
    @BindView(R.id.snack_bar_container)
    CoordinatorLayout snackBarContainer;

    private final String debugTag = "debugTag";

    private AddressListController controller;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        RouteInfoHolder routeInfoHolder = getArguments().getParcelable("routeInfoHolder");
        controller = new AddressListController(this, routeInfoHolder.getAddressList());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        controller.showAddressList();
    }

    @Override
    public void setupAdapter(AddressListAdapter adapter) {
        adapter.addContext(this.getContext());
        adapter.addTouchHelper(recyclerView);
        recyclerView.setAdapter(adapter);
    }

    @OnClick(R.id.address_input_btn)
    public void showAddressInputField(){
        controller.showInputField();
    }

    @Override
    public void addressDeleted(final int position, final Address address) {

        Snackbar snackbar = Snackbar.make(snackBarContainer, "Address deleted", Snackbar.LENGTH_LONG);

        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.restoreAddress();
                recyclerView.smoothScrollToPosition(position);
            }
        });
        snackbar.addCallback(new Snackbar.Callback(){
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                controller.removeAddressFromContainer(address);
            }
        });
        snackbar.show();
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
    public void scrollToItem(int position) {
        recyclerView.smoothScrollToPosition(position);
    }

    @Override
    public void showToast(String message) {
        Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}