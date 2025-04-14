package com.example.project_mobile.api;

import com.example.project_mobile.dto.AuthResponse;
import com.example.project_mobile.dto.LoginRequest;
import com.example.project_mobile.model.ParkingLot;
import com.example.project_mobile.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @GET("parking-lots/all")
    Call<List<ParkingLot>> getAllParkingLots();

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

}

