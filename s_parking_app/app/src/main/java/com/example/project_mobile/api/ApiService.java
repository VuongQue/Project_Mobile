package com.example.project_mobile.api;

import com.example.project_mobile.dto.AuthResponse;
import com.example.project_mobile.dto.LoginRequest;
import com.example.project_mobile.dto.UserInfoResponse;
import com.example.project_mobile.dto.UsernameRequest;
import com.example.project_mobile.dto.ParkingLotResponse;
import com.example.project_mobile.dto.SessionResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @GET("parking-lots/all")
    Call<List<ParkingLotResponse>> getAllParkingLots();

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("sessions/user")
    Call<List<SessionResponse>> getSession(@Body UsernameRequest request);

    @POST("user/profile")
    Call<UserInfoResponse> getUserInfo(@Body UsernameRequest request);

}

