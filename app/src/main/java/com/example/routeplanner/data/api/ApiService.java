package com.example.routeplanner.data.api;

import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.api.AddressRequest;
import com.example.routeplanner.data.pojos.api.ChangeAddressRequest;
import com.example.routeplanner.data.pojos.api.Container;
import com.example.routeplanner.data.pojos.api.Drive;
import com.example.routeplanner.data.pojos.api.DriveRequest;
import com.example.routeplanner.data.pojos.api.OrganizeRouteRequest;
import com.example.routeplanner.data.pojos.api.OrganizedRouteResponse;
import com.example.routeplanner.data.pojos.api.RemoveAddressRequest;
import com.example.routeplanner.data.pojos.api.UpdateDriveListRequest;
import com.example.routeplanner.data.pojos.api.UpdatePackageCountRequest;

import java.util.List;

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

    @POST("route")
    Call<OrganizedRouteResponse> sortRoute(@Body OrganizeRouteRequest request);

    @POST("packageCount")
    Call<Void> updatePackageCount(@Body UpdatePackageCountRequest request);

    @POST("updatedrivelist")
    Call<Void> updateDriveList(@Body UpdateDriveListRequest request);

    @POST("removeaddress")
    Call<Void> removeAddressRequest(@Body RemoveAddressRequest request);

    @POST("drive")
    Call<Drive> driveRequest(@Body DriveRequest request);
}
