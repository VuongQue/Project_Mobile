package com.example.project_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.project_mobile.adapter.SessionAdapter;
import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.databinding.ActivityHistoryBinding;
import com.example.project_mobile.dto.UsernameRequest;
import com.example.project_mobile.dto.SessionResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {
    private SessionAdapter adapter;
    private List<SessionResponse> sessionResponseList;
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

        sessionResponseList = new ArrayList<>();
        adapter = new SessionAdapter(this, sessionResponseList, new SessionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SessionResponse sessionResponse) {
                Toast.makeText(HistoryActivity.this, "Selected: " + sessionResponse.getId(), Toast.LENGTH_SHORT).show();
            }
        });
        binding.recyclerView.setAdapter(adapter);

        ApiService apiService = ApiClient.getInstance(getApplicationContext());
        String username = getSharedPreferences("LoginDetails", MODE_PRIVATE).getString("Username", "");
        apiService.getSession(new UsernameRequest(username)).enqueue(new Callback<List<SessionResponse>>() {
            @Override
            public void onResponse(Call<List<SessionResponse>> call, Response<List<SessionResponse>> response) {
                if (response.isSuccessful() & response.body() != null)
                {
                    sessionResponseList.clear();
                    sessionResponseList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
                else {
                    Log.e("API_ERROR", "Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<SessionResponse>> call, Throwable t) {
                Log.e("API_ERROR", "Failed to fetch data", t);
            }
        });

        adapter.notifyDataSetChanged();
    }
}
