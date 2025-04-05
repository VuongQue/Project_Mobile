package com.example.project_mobile;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.project_mobile.databinding.ActivityHistoryBinding;
import com.example.project_mobile.databinding.ActivityMainBinding;
import com.example.project_mobile.model.ParkingLot;
import com.example.project_mobile.model.Session;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

        sessionList.clear();
        sessionList.add(new Session(1, "SV001", "A1", new Date(2025 - 1900, 2, 9, 8, 30), new Date(2025 - 1900, 2, 9, 18, 45), false, 4000));
        sessionList.add(new Session(2, "SV002", "B2", new Date(2025 - 1900, 2, 10, 9, 15), new Date(2025 - 1900, 2, 10, 17, 30), true, 5000));
        sessionList.add(new Session(3, "SV003", "C3", new Date(2025 - 1900, 2, 11, 7, 50), new Date(2025 - 1900, 2, 11, 20, 10), false, 4500));
        sessionList.add(new Session(4, "SV004", "D4", new Date(2025 - 1900, 2, 12, 10, 5), new Date(2025 - 1900, 2, 12, 16, 40), true, 5500));

        adapter.notifyDataSetChanged();
    }
}
