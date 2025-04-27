package com.example.project_mobile.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.project_mobile.utils.LocalDateDeserializer;
import com.example.project_mobile.utils.LocalDateTimeDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL = "http://192.168.1.105:8080/";
    private static ApiService apiService;

    public static ApiService getInstance(final Context context) {
        if (apiService == null) {

            AuthInterceptor.TokenProvider tokenProvider = () -> {
                SharedPreferences prefs = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
                return prefs.getString("Token", "");
            };

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                    .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss") // Format y hệt như JSON từ backend
                    .create();

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(tokenProvider))
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}

