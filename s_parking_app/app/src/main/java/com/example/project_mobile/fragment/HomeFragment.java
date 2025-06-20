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
import com.example.project_mobile.LoginActivity;
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
import com.example.project_mobile.dto.UserInfoResponse;
import com.example.project_mobile.dto.UsernameRequest;
import com.example.project_mobile.model.Image;
import com.example.project_mobile.socket.WebSocketManager;
import com.example.project_mobile.storage.GuestManager;
import com.example.project_mobile.storage.PreferenceManager;
import com.google.gson.reflect.TypeToken;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderView;

import java.lang.reflect.Type;
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
    private PreferenceManager preferenceManager;
    private ArrayList<Image> imageList;
    private ArrayList<ParkingAreaResponse> parkingAreaResponseArrayList;
    private boolean isGuest;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(requireContext());
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

    @Override
    public void onResume() {
        super.onResume();
        isGuest = GuestManager.isGuest(requireContext());

        if (!isGuest)
        {
            loadUserInfo();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isGuest = GuestManager.isGuest(requireContext());

        if (!isGuest)
        {
            loadUserInfo();
            loadCurrentSession();
        }

        parkingAreaResponseArrayList = new ArrayList<>();
        imageList = new ArrayList<>();

        parkingAreaAdapter = new ParkingAreaAdapter(getContext(), parkingAreaResponseArrayList);

        binding.recyclerView.setAdapter(parkingAreaAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        parkingAreaAdapter.notifyDataSetChanged();

        loadParkingArea();
        loadNotificationImg();

        onClickListener();
        configSliderView();
    }

    private void loadUserInfo() {
        UserInfoResponse userInfo = preferenceManager.getUserInfo();
        if (userInfo != null)
        {
            String fullName = userInfo.getFullName();
            String avatarUrl = userInfo.getAvatarUrl();
            binding.fullName.setText(fullName);
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
        }
        else {
            Toast.makeText(getContext(), "NULl", Toast.LENGTH_SHORT).show();
        }
    }

    private void showQRCodeDialog() {
        // Tạo một Dialog
        Dialog qrDialog = new Dialog(requireContext());
        qrDialog.setContentView(R.layout.dialog_qr_code);

        String textToEncode = preferenceManager.getUsername();
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
        apiService.getParkingAreas().enqueue(new Callback<List<ParkingAreaResponse>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<ParkingAreaResponse>> call, @NonNull Response<List<ParkingAreaResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    parkingAreaResponseArrayList.clear();
                    parkingAreaResponseArrayList.addAll(response.body());
                    parkingAreaAdapter.notifyDataSetChanged();

                    // WebSocket chỉ được khởi tạo lần đầu
                    if (socket == null) {
                        startWebSocketUpdates();
                    }

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

    private WebSocketManager<List<ParkingAreaResponse>> socket;

    private void startWebSocketUpdates() {
        Type listType = new TypeToken<List<ParkingAreaResponse>>() {}.getType();

        socket = new WebSocketManager<>(listType, "/topic/parkingArea");

        socket.connect(new WebSocketManager.OnMessageCallback<List<ParkingAreaResponse>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onMessage(List<ParkingAreaResponse> updatedAreas) {
                requireActivity().runOnUiThread(() -> {
                    Log.d("WebSocket", "Received Data: " + updatedAreas);

                    parkingAreaResponseArrayList.clear();
                    parkingAreaResponseArrayList.addAll(updatedAreas);
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
        String username = preferenceManager.getUsername();
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
        binding.imageSliderMain.setSliderAdapter(sliderAdapter);

        // Cấu hình sliderView
        binding.imageSliderMain.setIndicatorAnimation(IndicatorAnimationType.WORM);
        binding.imageSliderMain.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        binding.imageSliderMain.setIndicatorSelectedColor(getResources().getColor(R.color.red));
        binding.imageSliderMain.setIndicatorUnselectedColor(Color.GRAY);
        binding.imageSliderMain.startAutoCycle();
        binding.imageSliderMain.setScrollTimeInSec(5);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void onClickListener() {
        binding.icReload.setOnClickListener(v -> {
            loadParkingArea();
            loadNotificationImg();
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

}