package com.example.project_mobile.fragment;

import static android.content.Context.MODE_PRIVATE;

import static com.example.project_mobile.utils.Format.formatDateTime;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.project_mobile.HistoryActivity;
import com.example.project_mobile.ParkingActivity;
import com.example.project_mobile.R;
import com.example.project_mobile.adapter.ParkingAreaAdapter;
import com.example.project_mobile.adapter.SliderAdapter;
import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.databinding.FragmentHomeBinding;
import com.example.project_mobile.dto.MyCurrentSessionResponse;
import com.example.project_mobile.dto.ParkingAreaResponse;
import com.example.project_mobile.dto.UsernameRequest;
import com.example.project_mobile.model.Image;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    private SliderAdapter sliderAdapter;
    private ParkingAreaAdapter parkingAreaAdapter;
    private List<Image> imageList;
    private ArrayList<ParkingAreaResponse> parkingAreaResponseArrayList;
    private ArrayList<Integer> intImgArrayList;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater
            , @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        SwitchActivity();
        ConfigSliderView();
        // recyclerView

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        parkingAreaResponseArrayList = new ArrayList<>();
        parkingAreaAdapter = new ParkingAreaAdapter(getContext(), parkingAreaResponseArrayList);
        binding.recyclerView.setAdapter(parkingAreaAdapter);
        parkingAreaAdapter.notifyDataSetChanged();

        LoadCurrentSession();
        LoadParkingArea();
        Toast.makeText(getContext(), "" + parkingAreaAdapter.getItemCount(), Toast.LENGTH_SHORT).show();
        binding.icReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadCurrentSession();
                LoadParkingArea();
            }
        });
    }

    private void LoadParkingArea() {
        ApiService apiService = ApiClient.getInstance(getContext());
        apiService.getParkingAreas().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<ParkingAreaResponse>> call, @NonNull Response<List<ParkingAreaResponse>> response) {
                if (response.isSuccessful() & response.body() != null) {
                    parkingAreaResponseArrayList.clear();
                    parkingAreaResponseArrayList.addAll(response.body());

                    parkingAreaAdapter.notifyDataSetChanged();

                } else {
                    Log.e("API_ERROR", "Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ParkingAreaResponse>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Failed to fetch data", t);
            }
        });
    }

    private void LoadCurrentSession() {
        String username = requireActivity().getSharedPreferences("LoginDetails", MODE_PRIVATE).getString("Username", "");
        ApiService apiService = ApiClient.getInstance(getContext());
        apiService.getMyCurrentSession(new UsernameRequest(username)).enqueue(new Callback<MyCurrentSessionResponse>() {
            @Override
            public void onResponse(@NonNull Call<MyCurrentSessionResponse> call, @NonNull Response<MyCurrentSessionResponse> response) {
                if (response.isSuccessful() & response.body() != null) {
                    binding.tvLocation.setText(String.valueOf(response.body().getLocation()));
                    LocalDateTime checkIn = response.body().getCheckIn();
                    LocalDateTime checkOut = response.body().getCheckOut();
                    binding.tvCheckIn.setText("Check in: " + formatDateTime(checkIn));
                    binding.tvCheckOut.setText("Check out: " + formatDateTime(checkOut));
                } else {
                    Log.e("API_ERROR", "Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MyCurrentSessionResponse> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Failed to fetch data", t);
            }
        });
    }

    private void ConfigSliderView() {
        intImgArrayList = new ArrayList<>();

        // Thêm ảnh vào danh sách
        intImgArrayList.add(R.drawable.notif1);
        intImgArrayList.add(R.drawable.notif2);
        intImgArrayList.add(R.drawable.notif3);

        sliderAdapter = new SliderAdapter(requireContext(), intImgArrayList);
        binding.imageSlider.setSliderAdapter(sliderAdapter);

        // Cấu hình sliderView
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
        binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        binding.imageSlider.setIndicatorSelectedColor(getResources().getColor(R.color.red));
        binding.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
        binding.imageSlider.startAutoCycle();
        binding.imageSlider.setScrollTimeInSec(5);
    }

    private void SwitchActivity() {
        if (binding != null) {
            binding.booking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ParkingActivity.class);
                    startActivity(intent);
                }
            });

            binding.history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), HistoryActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

}
