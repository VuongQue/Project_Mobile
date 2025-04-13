package com.example.project_mobile.api;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static ApiService apiService;

    public static ApiService getInstance(final Context context) {
        if (apiService == null) {

            AuthInterceptor.TokenProvider tokenProvider = () -> {
                SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                return prefs.getString("auth_token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMjExMDQxMiIsImlhdCI6MTc0NDU1Nzg0NywiZXhwIjoxNzQ0NTYxNDQ3fQ.DD0g2S6HQ67PUW5sxiunTJGzaWslzlp4nCI5UqtJt2E");
            };

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(tokenProvider))
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}

