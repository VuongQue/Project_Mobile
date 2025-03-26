package com.example.project_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.stream.Collectors;

public class ParkingActivity extends AppCompatActivity {
    AutoCompleteTextView actvArea, actvRow, actvPos;
    private RecyclerView recyclerView;
    private ParkingLotAdapter adapter;
    private ArrayAdapter<String> areaAdapter, rowAdapter, posAdapter;
    private List<String> areaList, rowList, posList;

    ImageButton btnBack;
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

        Mapping();
        SwitchActivity();
        Load();
        Filter();

    }

    public void Mapping() {
        btnBack = findViewById(R.id.btnBack);
        actvArea = findViewById(R.id.actvArea);
        actvRow = findViewById(R.id.actvRow);
        actvPos = findViewById(R.id.actvPos);
        recyclerView = findViewById(R.id.recyclerView);
    }

    public void Filter() {
        areaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, areaList);
        rowAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, rowList);
        posAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, posList);

        actvArea.setAdapter(areaAdapter);
        actvRow.setAdapter(rowAdapter);
        actvPos.setAdapter(posAdapter);

        actvArea.addTextChangedListener(filterWatcher);
        actvRow.addTextChangedListener(filterWatcher);
        actvPos.addTextChangedListener(filterWatcher);

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
        String selectedArea = actvArea.getText().toString().trim();
        String selectedRow = actvRow.getText().toString().trim();
        String selectedPos = actvPos.getText().toString().trim();

        // Lọc danh sách theo điều kiện
        List<ParkingLot> filteredList = parkingLotList.stream()
                .filter(lot -> (selectedArea.isEmpty() || lot.getArea().equalsIgnoreCase(selectedArea)) &&
                        (selectedRow.isEmpty() || lot.getRow().equalsIgnoreCase(selectedRow)) &&
                        (selectedPos.isEmpty() || lot.getPos().equalsIgnoreCase(selectedPos)))
                .collect(Collectors.toList());

        adapter = new ParkingLotAdapter(this, filteredList, new ParkingLotAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ParkingLot parkingLot) {
                Toast.makeText(ParkingActivity.this, "Selected: " + parkingLot.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Lấy danh sách bãi, hàng, vị trí sau khi lọc
        areaList = filteredList.stream().map(ParkingLot::getArea).distinct().collect(Collectors.toList());
        rowList = filteredList.stream().map(ParkingLot::getRow).distinct().collect(Collectors.toList());
        posList = filteredList.stream().map(ParkingLot::getPos).distinct().collect(Collectors.toList());

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
        parkingLotList.add(new ParkingLot("A", "D", "01", "Available"));
        parkingLotList.add(new ParkingLot("A", "E", "02", "Booked"));
        parkingLotList.add(new ParkingLot("A", "F", "03", "Unavailable"));
        parkingLotList.add(new ParkingLot("B", "D", "04", "Available"));
        parkingLotList.add(new ParkingLot("B", "G", "05", "Booked"));

        adapter.notifyDataSetChanged();

    }

    public void SwitchActivity() {

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ParkingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
