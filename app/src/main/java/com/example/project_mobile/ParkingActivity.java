package com.example.project_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
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
import com.example.project_mobile.model.ParkingLot;

import java.util.ArrayList;
import java.util.List;

public class ParkingActivity extends AppCompatActivity {
    AutoCompleteTextView spinnerBaiXe;
    AutoCompleteTextView spinnerHangXe;
    AutoCompleteTextView spinnerViTri;
    private RecyclerView recyclerView;
    private ParkingLotAdapter adapter;
    private List<ParkingLot> parkingLotList;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_parking);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SwitchActivity();
        Load();
        //SetUp();

    }

    private void Load() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        parkingLotList = new ArrayList<>();
        adapter = new ParkingLotAdapter(this, parkingLotList, new ParkingLotAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ParkingLot parkingLot) {
                Toast.makeText(ParkingActivity.this, "Selected: " + parkingLot.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);

        parkingLotList.clear();
        parkingLotList.add(new ParkingLot("A", "1", "01", "Available"));
        parkingLotList.add(new ParkingLot("A", "1", "02", "Booked"));
        parkingLotList.add(new ParkingLot("A", "2", "03", "Unavailable"));
        parkingLotList.add(new ParkingLot("B", "1", "04", "Available"));
        parkingLotList.add(new ParkingLot("B", "2", "05", "Booked"));

        adapter.notifyDataSetChanged();

    }

    private void SetUp() {
        String[] danhSachViTri = {"1", "2", "3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, danhSachViTri);

        spinnerViTri = findViewById(R.id.spinnerViTri);
        spinnerViTri.setAdapter(adapter);

    }

    private void SwitchActivity() {
        final ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ParkingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
