package com.example.project_mobile.api;

import android.content.Context;

import com.example.project_mobile.storage.PreferenceManager;
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

//    public static final String BASE_URL = "http://10.0.2.2:8080/";
    public static final String BASE_URL = "http://192.168.43.237:8080/";
    private static ApiService apiService;

    public static ApiService getInstance(final Context context) {
        if (apiService == null) {

            // Sử dụng PreferenceManager để lấy token
            PreferenceManager preferenceManager = new PreferenceManager(context);

            AuthInterceptor.TokenProvider tokenProvider = () -> {
                // Không cần try-catch vì PreferenceManager đã xử lý
                return preferenceManager.getToken();
            };

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                    .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
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
