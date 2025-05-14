package com.example.project_mobile.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.project_mobile.HistoryActivity;
import com.example.project_mobile.LoginActivity;
import com.example.project_mobile.ParkingActivity;
import com.example.project_mobile.PaymentActivity;
import com.example.project_mobile.R;
import com.example.project_mobile.adapter.ParkingAreaAdapter;
import com.example.project_mobile.adapter.SliderAdapter;
import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.databinding.FragmentHomeBinding;
import com.example.project_mobile.model.Image;
import com.example.project_mobile.storage.GuestManager;
import com.example.project_mobile.dto.ParkingAreaResponse;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SliderAdapter sliderAdapter;
    private ParkingAreaAdapter parkingAreaAdapter;
    private ArrayList<Image> imageList;
    private ArrayList<ParkingAreaResponse> parkingAreaList;
    private boolean isGuest;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        isGuest = GuestManager.isGuest(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageList = new ArrayList<>();
        parkingAreaList = new ArrayList<>();
        parkingAreaAdapter = new ParkingAreaAdapter(requireContext(), parkingAreaList);

        binding.recyclerView.setAdapter(parkingAreaAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        setupSliderView();
        loadParkingAreaData();
        loadNotificationImages();

        setClickListeners();
    }

    /**
     * Điều hướng đến Login nếu đang ở chế độ Guest
     */
    private void navigateToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    /**
     * Thiết lập các sự kiện click
     */
    private void setClickListeners() {
        binding.icReload.setOnClickListener(v -> {
            loadParkingAreaData();
            loadNotificationImages();
        });

        binding.booking.setOnClickListener(v -> {
            if (isGuest) {
                navigateToLogin();
            } else {
                startActivity(new Intent(requireContext(), ParkingActivity.class));
            }
        });

        binding.history.setOnClickListener(v -> {
            if (isGuest) {
                navigateToLogin();
            } else {
                startActivity(new Intent(requireContext(), HistoryActivity.class));
            }
        });

        binding.payment.setOnClickListener(v -> {
            if (isGuest) {
                navigateToLogin();
            } else {
                startActivity(new Intent(requireContext(), PaymentActivity.class));
            }
        });

        binding.qr.setOnClickListener(v -> {
            if (isGuest) {
                navigateToLogin();
            } else {
                showQRCodeDialog();
            }
        });
    }

    /**
     * Hiển thị mã QR
     */
    private void showQRCodeDialog() {
        Dialog qrDialog = new Dialog(requireContext());
        qrDialog.setContentView(R.layout.dialog_qr_code);

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap("SampleQRCodeData", com.google.zxing.BarcodeFormat.QR_CODE, 400, 400);

            ImageView qrImageView = qrDialog.findViewById(R.id.qrImageView);
            qrImageView.setImageBitmap(bitmap);
            qrDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Không thể tạo QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Tải danh sách bãi đỗ xe
     */
    private void loadParkingAreaData() {
        ApiService apiService = ApiClient.getInstance(requireContext());
        apiService.getParkingAreas().enqueue(new Callback<List<ParkingAreaResponse>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<ParkingAreaResponse>> call, @NonNull Response<List<ParkingAreaResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    parkingAreaList.clear();
                    parkingAreaList.addAll(response.body());
                    parkingAreaAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ParkingAreaResponse>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Failed to load parking areas", t);
            }
        });
    }

    /**
     * Tải hình ảnh thông báo
     */
    private void loadNotificationImages() {
        ApiService apiService = ApiClient.getInstance(requireContext());
        apiService.getNotificationImg().enqueue(new Callback<List<Image>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Image>> call, @NonNull Response<List<Image>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    imageList.clear();
                    imageList.addAll(response.body());
                    sliderAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Image>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Failed to load images", t);
            }
        });
    }

    /**
     * Thiết lập Slider View
     */
    private void setupSliderView() {
        sliderAdapter = new SliderAdapter(requireContext(), imageList);
        binding.imageSlider.setSliderAdapter(sliderAdapter);

        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
        binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        binding.imageSlider.setIndicatorSelectedColor(getResources().getColor(R.color.red));
        binding.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
        binding.imageSlider.startAutoCycle();
        binding.imageSlider.setScrollTimeInSec(5);
    }
}