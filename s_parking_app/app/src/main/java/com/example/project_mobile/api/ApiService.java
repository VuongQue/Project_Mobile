package com.example.project_mobile.api;

import com.example.project_mobile.dto.AuthResponse;
import com.example.project_mobile.dto.BookingResponse;
import com.example.project_mobile.dto.LoginRequest;
import com.example.project_mobile.dto.MyCurrentSessionResponse;
import com.example.project_mobile.dto.NotificationRequest;
import com.example.project_mobile.dto.NotificationResponse;
import com.example.project_mobile.dto.ParkingAreaResponse;
import com.example.project_mobile.dto.PaymentRequest;
import com.example.project_mobile.dto.PaymentResponse;
import com.example.project_mobile.dto.SuccessResponse;
import com.example.project_mobile.dto.UpdateAvatarRequest;
import com.example.project_mobile.dto.UserInfoResponse;
import com.example.project_mobile.dto.UsernameRequest;
import com.example.project_mobile.dto.ParkingLotResponse;
import com.example.project_mobile.dto.SessionResponse;
import com.example.project_mobile.model.Image;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ApiService {

    @GET("parking-lots/available")
    Call<List<ParkingLotResponse>> getAvailableParkingLots();

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("sessions/user")
    Call<List<SessionResponse>> getSession(@Body UsernameRequest request);

    @POST("user/profile")
    Call<UserInfoResponse> getUserInfo(@Body UsernameRequest request);

    @POST("sessions/my-current-session")
    Call<MyCurrentSessionResponse> getMyCurrentSession(@Body UsernameRequest request);

    @GET("parking-area/all")
    Call<List<ParkingAreaResponse>> getParkingAreas();

    @GET("img/all")
    Call<List<Image>> getNotificationImg();

    @PUT("user/update-avatar")
    Call<SuccessResponse> updateAvatar(@Body UpdateAvatarRequest request);

    @POST("notification/yours")
    Call<List<NotificationResponse>> getMyNotifications(@Body UsernameRequest request);

    @POST("notification/update")
    Call<SuccessResponse> updateNotificationStatus(@Body NotificationRequest request);

    @POST("booking/my-booking-history")
    Call<List<BookingResponse>> getMyBookingHistory(@Body UsernameRequest request);

    @POST("sessions/unpaid")
    Call<List<SessionResponse>> getUnpaidSessions(@Body UsernameRequest usernameRequest);

    @POST("/payment/create-transaction")
    Call<PaymentResponse> createPayment(@Body PaymentRequest request);
    @PUT("/payment/confirm")
    Call<String> confirmPayment(@Body PaymentRequest paymentRequest);

}

