package com.example.project_mobile.api;

import com.example.project_mobile.dto.VietQRResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import java.util.Map;

public interface VietQRService {
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    @POST("v2/generate")
    Call<VietQRResponse> generateQR(@Body Map<String, Object> body);
}
