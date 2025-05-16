package com.example.project_mobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.project_mobile.adapter.BookingAdapter;
import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.databinding.ActivityBookingHistoryBinding;
import com.example.project_mobile.dto.BookingResponse;
import com.example.project_mobile.dto.UsernameRequest;
import com.example.project_mobile.storage.PreferenceManager;
import com.example.project_mobile.utils.LocalHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingHistoryActivity extends AppCompatActivity {
    ActivityBookingHistoryBinding binding;
    BookingAdapter adapter;
    List<BookingResponse> bookingResponseList;
    String username;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        username = preferenceManager.getUsername();
        binding = ActivityBookingHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize list and adapter
        bookingResponseList = new ArrayList<>();
        adapter = new BookingAdapter(BookingHistoryActivity.this, bookingResponseList);

        // Set up RecyclerView
        binding.rvBookingHistory.setAdapter(adapter);
        binding.rvBookingHistory.setLayoutManager(new LinearLayoutManager(this));

        // Load booking data
        load();

        SwitchActivity();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalHelper.setLocale(newBase));
    }

    private void SwitchActivity() {
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BookingHistoryActivity.this, ParkingActivity.class));
                finish();
            }
        });
    }

    private void load() {
        // Show loading indicator while fetching data
        binding.progressBar.setVisibility(View.VISIBLE);

        ApiService apiService = ApiClient.getInstance(this);
        UsernameRequest request = new UsernameRequest(username);
        apiService.getMyBookingHistory(request).enqueue(new Callback<List<BookingResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<BookingResponse>> call, @NonNull Response<List<BookingResponse>> response) {
                // Hide loading indicator
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Update RecyclerView with new data
                    bookingResponseList.clear();
                    bookingResponseList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    // Handle case when no booking history found
                    Log.e("API_ERROR", "No bookings found or error code: " + response.code());
                    // Optionally show a Toast or empty state in RecyclerView
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<BookingResponse>> call, @NonNull Throwable t) {
                // Hide loading indicator and log error
                binding.progressBar.setVisibility(View.GONE);
                Log.e("API_ERROR", "Failed to fetch data", t);
                // Optionally show an error message to the user
            }
        });
    }
}
