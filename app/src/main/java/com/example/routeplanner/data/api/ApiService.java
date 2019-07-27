package com.example.routeplanner.data.api;

import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.api.AddressRequest;
import com.example.routeplanner.data.pojos.api.ChangeAddressRequest;
import com.example.routeplanner.data.pojos.api.Container;
import com.example.routeplanner.data.pojos.api.Drive;
import com.example.routeplanner.data.pojos.api.DriveRequest;
import com.example.routeplanner.data.pojos.api.RemoveAddressRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @GET("container/{username}")
    Call<Container> containerRequest(@Path("username")String username);

    @POST("address")
    Call<Address> addressRequest(@Body AddressRequest request);

    @POST("changeaddress")
    Call<Address> changeAddressRequest(@Body ChangeAddressRequest request);

    @POST("removeaddress")
    Call<Void> removeAddressRequest(@Body RemoveAddressRequest request);

    @POST("drive")
    Call<Drive> driveRequest(@Body DriveRequest request);
}
