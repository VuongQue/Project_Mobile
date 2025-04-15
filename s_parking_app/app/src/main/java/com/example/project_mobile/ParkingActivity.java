package com.example.project_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.project_mobile.adapter.ParkingLotAdapter;
import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.databinding.ActivityParkingBinding;
import com.example.project_mobile.dto.ParkingLotResponse;

import java.util.ArrayList;
import java.util.List;
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
                Toast.makeText(ParkingActivity.this, "Selected: " + parkingLotResponse.getName(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ParkingActivity.this, "Selected: " + parkingLotResponse.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        binding.recyclerView.setAdapter(adapter);

        ApiService apiService = ApiClient.getInstance(getApplicationContext());
        apiService.getAllParkingLots().enqueue(new Callback<List<ParkingLotResponse>>() {
            @Override
            public void onResponse(Call<List<ParkingLotResponse>> call, Response<List<ParkingLotResponse>> response) {
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
            public void onFailure(Call<List<ParkingLotResponse>> call, Throwable t) {
                Log.e("API_ERROR", "Failed to fetch data", t);
            }
        });

    }

    public void SwitchActivity() {

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ParkingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
