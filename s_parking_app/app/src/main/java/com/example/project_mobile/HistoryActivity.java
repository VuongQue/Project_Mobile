package com.example.project_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_mobile.adapter.ParkingLotAdapter;
import com.example.project_mobile.adapter.SessionAdapter;
import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.databinding.ActivityHistoryBinding;
import com.example.project_mobile.databinding.ActivityMainBinding;
import com.example.project_mobile.dto.UsernameRequest;
import com.example.project_mobile.model.ParkingLot;
import com.example.project_mobile.model.Session;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {
    private SessionAdapter adapter;
    private List<Session> sessionList;
    ActivityHistoryBinding binding;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Load();
        SwitchActivity();
    }
    public void SwitchActivity() {

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void Load() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        sessionList = new ArrayList<>();
        adapter = new SessionAdapter(this, sessionList, new SessionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Session session) {
                Toast.makeText(HistoryActivity.this, "Selected: " + session.getId(), Toast.LENGTH_SHORT).show();
            }
        });
        binding.recyclerView.setAdapter(adapter);

        ApiService apiService = ApiClient.getInstance(getApplicationContext());
        String username = getSharedPreferences("LoginDetails", MODE_PRIVATE).getString("Username", "");
        apiService.getSession(new UsernameRequest(username)).enqueue(new Callback<List<Session>>() {
            @Override
            public void onResponse(Call<List<Session>> call, Response<List<Session>> response) {
                if (response.isSuccessful() & response.body() != null)
                {
                    sessionList.clear(); // Clear dữ liệu cũ
                    sessionList.addAll(response.body()); // Add dữ liệu mới
                    adapter.notifyDataSetChanged();
                }
                else {
                    Log.e("API_ERROR", "Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Session>> call, Throwable t) {
                Log.e("API_ERROR", "Failed to fetch data", t);
            }
        });

        adapter.notifyDataSetChanged();
    }
}
