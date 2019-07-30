package com.example.routeplanner.features.container.addressDetailsFragment;

import com.example.routeplanner.data.database.DatabaseCallback;
import com.example.routeplanner.data.database.DatabaseService;
import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.database.AddressInformationResponse;
import com.example.routeplanner.data.pojos.database.AddressTypeResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressDetailsModel implements MvcAddressDetails.Model {

    private DatabaseService databaseService;

    AddressDetailsModel(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public void getAddressInformation(Address address, final DatabaseCallback.AddressInformationCallBack callback) {

        Call<AddressInformationResponse> call = databaseService.getAddressInformation(
                address.getStreet(),
                address.getPostCode(),
                address.getCity()
        );

        call.enqueue(new Callback<AddressInformationResponse>() {
            @Override
            public void onResponse(Call<AddressInformationResponse> call, Response<AddressInformationResponse> response) {
                callback.onAddressInformationResponse(response.body());
            }

            @Override
            public void onFailure(Call<AddressInformationResponse> call, Throwable t) {
                callback.onAddressInformationResponseFailure();
            }
        });
    }

    @Override
    public void changeAddressType(String username, Address address, final DatabaseCallback.AddressTypeChangeCallback callback) {
        Call<AddressTypeResponse> call = databaseService.changeAddressType(
                address.getStreet(),
                address.getPostCode(),
                address.getCity(),
                username
        );

        call.enqueue(new Callback<AddressTypeResponse>() {
            @Override
            public void onResponse(Call<AddressTypeResponse> call, Response<AddressTypeResponse> response) {
                callback.typeChangeResponse(response.body());
            }

            @Override
            public void onFailure(Call<AddressTypeResponse> call, Throwable t) {
                callback.typeChangeResponseFailure();
            }
        });
    }

    @Override
    public void changeOpeningTime(Address address, int openingTime) {

        Call<Void> call = databaseService.changeOpeningTime(
                address.getStreet(),
                address.getPostCode(),
                address.getCity(),
                openingTime
        );

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    @Override
    public void changeClosingTime(Address address, int closingTime) {
        Call<Void> call = databaseService.changeClosingTime(
                address.getStreet(),
                address.getPostCode(),
                address.getCity(),
                closingTime
        );

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}
