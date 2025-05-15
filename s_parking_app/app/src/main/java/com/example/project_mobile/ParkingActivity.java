package com.example.project_mobile;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.project_mobile.adapter.ParkingLotAdapter;
import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.databinding.ActivityParkingBinding;
import com.example.project_mobile.databinding.DialogBookingBinding;
import com.example.project_mobile.dto.BookingRequest;
import com.example.project_mobile.dto.BookingResponse;
import com.example.project_mobile.dto.ParkingLotResponse;
import com.example.project_mobile.utils.LocalHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParkingActivity extends AppCompatActivity {
    private ParkingLotAdapter adapter;
    private ArrayAdapter<String> areaAdapter, rowAdapter, posAdapter;
    private List<String> areaList, rowList, posList;

    ActivityParkingBinding binding;
    private List<ParkingLotResponse> parkingLotResponseList;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_parking);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivityParkingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SwitchActivity();
        Load();
        Filter();

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalHelper.setLocale(newBase));
    }

    public void Filter() {
        areaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, areaList);
        rowAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, rowList);
        posAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, posList);

        binding.actvArea.setAdapter(areaAdapter);
        binding.actvRow.setAdapter(rowAdapter);
        binding.actvPos.setAdapter(posAdapter);

        binding.actvArea.addTextChangedListener(filterWatcher);
        binding.actvRow.addTextChangedListener(filterWatcher);
        binding.actvPos.addTextChangedListener(filterWatcher);

    }

    public final TextWatcher filterWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateLists();
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    @SuppressLint("NotifyDataSetChanged")
    public void updateLists() {
        String selectedArea = binding.actvArea.getText().toString().trim();
        String selectedRow = binding.actvRow.getText().toString().trim();
        String selectedPos = binding.actvPos.getText().toString().trim();

        // Lọc danh sách theo điều kiện
        List<ParkingLotResponse> filteredList = parkingLotResponseList.stream()
                .filter(lot -> (selectedArea.isEmpty() || lot.getArea().equalsIgnoreCase(selectedArea)) &&
                        (selectedRow.isEmpty() || lot.getRow().equalsIgnoreCase(selectedRow)) &&
                        (selectedPos.isEmpty() || lot.getPos().equalsIgnoreCase(selectedPos)))
                .collect(Collectors.toList());

        adapter = new ParkingLotAdapter(this, filteredList, new ParkingLotAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ParkingLotResponse parkingLotResponse) {
                showDialog(parkingLotResponse);
            }
        });
        binding.recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Lấy danh sách bãi, hàng, vị trí sau khi lọc
        areaList = filteredList.stream().map(ParkingLotResponse::getArea).distinct().collect(Collectors.toList());
        rowList = filteredList.stream().map(ParkingLotResponse::getRow).distinct().collect(Collectors.toList());
        posList = filteredList.stream().map(ParkingLotResponse::getPos).distinct().collect(Collectors.toList());

        // Cập nhật dữ liệu cho Adapter
        areaAdapter.clear();
        areaAdapter.addAll(areaList);
        areaAdapter.notifyDataSetChanged();

        rowAdapter.clear();
        rowAdapter.addAll(rowList);
        rowAdapter.notifyDataSetChanged();

        posAdapter.clear();
        posAdapter.addAll(posList);
        posAdapter.notifyDataSetChanged();
    }

    public void Load() {
        areaList  = new ArrayList<>();
        rowList = new ArrayList<>();
        posList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        parkingLotResponseList = new ArrayList<>();
        adapter = new ParkingLotAdapter(this, parkingLotResponseList, new ParkingLotAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ParkingLotResponse parkingLotResponse) {
                showDialog(parkingLotResponse);
            }
        });
        binding.recyclerView.setAdapter(adapter);

        ApiService apiService = ApiClient.getInstance(getApplicationContext());
        apiService.getAvailableParkingLots().enqueue(new Callback<List<ParkingLotResponse>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<ParkingLotResponse>> call, @NonNull Response<List<ParkingLotResponse>> response) {
                if (response.isSuccessful() & response.body() != null)
                {
                    parkingLotResponseList.clear(); // Clear dữ liệu cũ
                    parkingLotResponseList.addAll(response.body()); // Add dữ liệu mới
                    adapter.notifyDataSetChanged();
                }
                else {
                    Log.e("API_ERROR", "Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ParkingLotResponse>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Failed to fetch data", t);
            }
        });

    }

    private void showDialog(ParkingLotResponse parkingLotResponse) {
        DialogBookingBinding bookingBinding = DialogBookingBinding.inflate(getLayoutInflater());

        bookingBinding.etLocation.setText(parkingLotResponse.getLocation());

        Dialog bookingDialog = new Dialog(ParkingActivity.this);
        bookingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bookingDialog.setContentView(bookingBinding.getRoot());

        Objects.requireNonNull(bookingDialog.getWindow()).setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        // Gán sự kiện click vào ô Date để chọn ngày
        bookingBinding.etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        bookingBinding.etDate.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Xử lý sự kiện Đặt chỗ
        bookingBinding.btnPay.setOnClickListener(v -> {
            String date = bookingBinding.etDate.getText().toString().trim();

            if (date.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ngày", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi API booking
            String username = getSharedPreferences("LoginDetails", MODE_PRIVATE)
                    .getString("Username", "");

            long idParking = parkingLotResponse.getId();

            ApiService apiService = ApiClient.getInstance(getApplicationContext());

            BookingRequest bookingRequest = new BookingRequest(idParking, username);

            apiService.createBooking(bookingRequest).enqueue(new Callback<BookingResponse>() {
                @Override
                public void onResponse(@NonNull Call<BookingResponse> call, @NonNull Response<BookingResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(ParkingActivity.this, "Đặt chỗ thành công!", Toast.LENGTH_SHORT).show();
                        bookingDialog.dismiss();

                        // Chuyển sang màn hình thanh toán
                        Intent intent = new Intent(ParkingActivity.this, PaymentActivity.class);
                        intent.putExtra("bookingId", response.body().getId());
                        intent.putExtra("fee", response.body().getFee());
                        intent.putExtra("isFromBooking", true);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ParkingActivity.this, "Đặt chỗ thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BookingResponse> call, @NonNull Throwable t) {
                    Toast.makeText(ParkingActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Hiển thị Dialog
        bookingDialog.show();
    }



    public void SwitchActivity() {

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ParkingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        binding.imgHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ParkingActivity.this, BookingHistoryActivity.class));
            }
        });
    }
}
