package com.example.project_mobile.fragment;

import static android.content.Context.MODE_PRIVATE;

import static com.example.project_mobile.utils.FormatAndCipher.formatDateTime;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
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
import com.example.project_mobile.ParkingActivity;
import com.example.project_mobile.PaymentActivity;
import com.example.project_mobile.R;
import com.example.project_mobile.adapter.ParkingAreaAdapter;
import com.example.project_mobile.adapter.SliderAdapter;
import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.databinding.FragmentHomeBinding;
import com.example.project_mobile.dto.MyCurrentSessionResponse;
import com.example.project_mobile.dto.NotificationResponse;
import com.example.project_mobile.dto.ParkingAreaResponse;
import com.example.project_mobile.dto.UsernameRequest;
import com.example.project_mobile.model.Image;
import com.example.project_mobile.socket.WebSocketManager;
import com.journeyapps.barcodescanner.BarcodeEncoder;
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
    private SharedPreferences sharedPreferences;
    private ArrayList<Image> imageList;
    private ArrayList<ParkingAreaResponse> parkingAreaResponseArrayList;

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
        // recyclerView

        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String fullName = requireActivity().getSharedPreferences("UserInfo", MODE_PRIVATE).getString("FullName", "");
        binding.fullName.setText(fullName);
        String avatarUrl = requireActivity().getSharedPreferences("UserInfo", MODE_PRIVATE).getString("Avatar_Url", "");

        if (!avatarUrl.isEmpty()) {
            Glide.with(requireContext())
                    .load(Uri.parse(avatarUrl))  // Tải ảnh từ URL
                    .into(binding.avatar);  // Gán vào ImageView
        } else {
            // Nếu không có URL hợp lệ, có thể sử dụng ảnh mặc định
            Glide.with(requireContext())
                    .load(android.R.drawable.sym_def_app_icon)  // Sử dụng ảnh mặc định từ drawable
                    .into(binding.avatar);
        }

        parkingAreaResponseArrayList = new ArrayList<>();
        imageList = new ArrayList<>();

        parkingAreaAdapter = new ParkingAreaAdapter(getContext(), parkingAreaResponseArrayList);

        binding.recyclerView.setAdapter(parkingAreaAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        parkingAreaAdapter.notifyDataSetChanged();

        loadCurrentSession();
        loadParkingArea();
        loadNotificationImg();

        onClickListener();
        configSliderView();
    }

    private void showQRCodeDialog() {
        // Tạo một Dialog
        Dialog qrDialog = new Dialog(requireContext());
        qrDialog.setContentView(R.layout.dialog_qr_code);

        String textToEncode = requireActivity().getSharedPreferences("LoginDetails", MODE_PRIVATE).getString("Username", "");
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(textToEncode, com.google.zxing.BarcodeFormat.QR_CODE, 400, 400);

            // Lấy ImageView từ Dialog và đặt hình ảnh QR Code
            ImageView qrImageView = qrDialog.findViewById(R.id.qrImageView);
            qrImageView.setImageBitmap(bitmap);

            // Hiển thị Dialog
            qrDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadNotificationImg() {
        ApiService apiService = ApiClient.getInstance(getContext());
        apiService.getNotificationImg().enqueue(new Callback<List<Image>>() {
            @Override
            public void onResponse(@NonNull Call<List<Image>> call, @NonNull Response<List<Image>> response) {
                if (response.isSuccessful() & response.body() != null) {
                    imageList.clear();
                    imageList.addAll(response.body());

                    sliderAdapter.notifyDataSetChanged();
                }
                else {
                    Log.e("API_ERROR", "Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Image>> call, Throwable t) {
                Log.e("API_ERROR", "Failed to fetch data", t);
            }
        });
    }

    private void loadParkingArea() {
        ApiService apiService = ApiClient.getInstance(getContext());
        apiService.getParkingAreas().enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<ParkingAreaResponse>> call, @NonNull Response<List<ParkingAreaResponse>> response) {
                if (response.isSuccessful() & response.body() != null) {
                    parkingAreaResponseArrayList.clear();
                    parkingAreaResponseArrayList.addAll(response.body());

                    parkingAreaAdapter.notifyDataSetChanged();
                    startWebSocketUpdates();

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
    private void startWebSocketUpdates() {
        WebSocketManager<ParkingAreaResponse> socket =
                new WebSocketManager<>(ParkingAreaResponse.class, "/topic/parkingArea");

        socket.connect(new WebSocketManager.OnMessageCallback<ParkingAreaResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onMessage(ParkingAreaResponse updatedArea) {
                requireActivity().runOnUiThread(() -> {
                    boolean updated = false;
                    for (int i = 0; i < parkingAreaResponseArrayList.size(); i++) {
                        ParkingAreaResponse current = parkingAreaResponseArrayList.get(i);
                        if (current.getIdArea().equals(updatedArea.getIdArea())) {
                            parkingAreaResponseArrayList.set(i, updatedArea);
                            updated = true;
                            break;
                        }
                    }

                    // Nếu không có trong list, thêm mới (optional)
                    if (!updated) {
                        parkingAreaResponseArrayList.add(updatedArea);
                    }
                    parkingAreaAdapter.notifyDataSetChanged();
                });
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setUpSession(MyCurrentSessionResponse response) {
        binding.tvLocation.setText(String.valueOf(response.getLocation()));
        LocalDateTime checkIn = response.getCheckIn();
        LocalDateTime checkOut = response.getCheckOut();
        binding.tvCheckIn.setText("Check in: " + formatDateTime(checkIn));
        binding.tvCheckOut.setText("Check out: " + formatDateTime(checkOut));
        if (checkOut == null) {
            binding.status.setText(R.string.parking_the_car);
        }
        else {
            binding.status.setText(R.string.no_parking);
        }
    }
    private void loadCurrentSession() {
        String username = requireActivity().getSharedPreferences("LoginDetails", MODE_PRIVATE).getString("Username", "");
        ApiService apiService = ApiClient.getInstance(getContext());
        apiService.getMyCurrentSession(new UsernameRequest(username)).enqueue(new Callback<MyCurrentSessionResponse>() {
            @Override
            public void onResponse(@NonNull Call<MyCurrentSessionResponse> call, @NonNull Response<MyCurrentSessionResponse> response) {
                if (response.isSuccessful() & response.body() != null) {
                    setUpSession(response.body());
                    startCheckInSocket(username);
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

    private void startCheckInSocket(String username) {
        WebSocketManager<MyCurrentSessionResponse> sessionSocket =
                new WebSocketManager<>(MyCurrentSessionResponse.class, "/topic/checkin/" + username);

        WebSocketManager<NotificationResponse> notiSocket =
                new WebSocketManager<>(NotificationResponse.class, "/topic/notification/" + username);

        sessionSocket.connect(new WebSocketManager.OnMessageCallback<MyCurrentSessionResponse>() {
            @Override
            public void onMessage(MyCurrentSessionResponse myCurrentSessionResponse) {
                requireActivity().runOnUiThread(() -> {
                    setUpSession(myCurrentSessionResponse);
                });
            }
        });

        notiSocket.connect(new WebSocketManager.OnMessageCallback<NotificationResponse>() {
            @Override
            public void onMessage(NotificationResponse notificationResponse) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), notificationResponse.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });

    }


    private void configSliderView() {
        sliderAdapter = new SliderAdapter(requireContext(), imageList);
        binding.imageSlider.setSliderAdapter(sliderAdapter);

        // Cấu hình sliderView
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
        binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        binding.imageSlider.setIndicatorSelectedColor(getResources().getColor(R.color.red));
        binding.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
        binding.imageSlider.startAutoCycle();
        binding.imageSlider.setScrollTimeInSec(5);
    }

    private void onClickListener() {
        if (binding != null) {
            binding.icReload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadCurrentSession();
                    loadParkingArea();
                    loadNotificationImg();
                }
            });
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

            binding.qr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showQRCodeDialog();
                }
            });

            binding.payment.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), PaymentActivity.class);
                startActivity(intent);
            });
        }
    }

}
