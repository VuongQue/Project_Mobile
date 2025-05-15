package com.example.project_mobile;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.project_mobile.adapter.BookingAdapter;
import com.example.project_mobile.adapter.SessionSelectAdapter;
import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.databinding.ActivityPaymentBinding;
import com.example.project_mobile.dto.BookingRequest;
import com.example.project_mobile.dto.BookingResponse;
import com.example.project_mobile.dto.PaymentRequest;
import com.example.project_mobile.dto.PaymentResponse;
import com.example.project_mobile.dto.SessionResponse;
import com.example.project_mobile.dto.UsernameRequest;
import com.example.project_mobile.utils.LocalHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {

    private ActivityPaymentBinding binding;
    private BookingAdapter bookingAdapter;
    private SessionSelectAdapter sessionAdapter;
    private final List<BookingResponse> bookingList = new ArrayList<>();
    private final List<SessionResponse> sessionList = new ArrayList<>();
    private double totalAmount = 0.0;
    private ApiService apiService;
    private long bookingId;
    private boolean isFromBooking = false;

    private static final String PREFS_PAYMENT = "PaymentPrefs";
    private static final String KEY_TRANSACTION_ID = "transactionId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        apiService = ApiClient.getInstance(getApplicationContext());

        isFromBooking = getIntent().getBooleanExtra("isFromBooking", false);
        bookingId = getIntent().getLongExtra("bookingId", -1);

        setupRecyclerView();
        setupEvents();

        if (isFromBooking) {
            loadSingleBooking(bookingId);
        } else {
            loadUnpaidSessions();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("PaymentActivity", "onResume called");
        Toast.makeText(this, "onResume called", Toast.LENGTH_SHORT).show();

        checkAndNotifyPaymentStatus();
    }

    private void checkAndNotifyPaymentStatus() {
        SharedPreferences prefs = getSharedPreferences(PREFS_PAYMENT, MODE_PRIVATE);
        String transactionId = prefs.getString(KEY_TRANSACTION_ID, null);

        if (transactionId != null) {
            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setTransactionId(transactionId);

            apiService.confirmZaloPayment(paymentRequest).enqueue(new Callback<PaymentResponse>() {
                @Override
                public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                    Log.d("PaymentActivity", "API confirmZaloPayment onResponse, success: " + response.isSuccessful());
                    if (response.isSuccessful()) {
                        Toast.makeText(PaymentActivity.this, "Cập nhật trạng thái thanh toán thành công", Toast.LENGTH_SHORT).show();
                        // Xóa transactionId để tránh gọi lại nhiều lần
                        prefs.edit().remove(KEY_TRANSACTION_ID).apply();
                    } else {
                        Toast.makeText(PaymentActivity.this, "Cập nhật trạng thái thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PaymentResponse> call, Throwable t) {
                    Toast.makeText(PaymentActivity.this, "Lỗi kết nối khi cập nhật trạng thái", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalHelper.setLocale(newBase));
    }

    private void setupRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (isFromBooking) {
            bookingAdapter = new BookingAdapter(this, bookingList);
            binding.recyclerView.setAdapter(bookingAdapter);
        } else {
            sessionAdapter = new SessionSelectAdapter(sessionList, selectedSessions -> {
                updateTotalAmount();
            });
            binding.recyclerView.setAdapter(sessionAdapter);
        }
    }

    private void setupEvents() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.cbAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isFromBooking) {
                sessionAdapter.selectAll(isChecked);
            }
        });

        binding.btnPay.setOnClickListener(v -> {
            if (isFromBooking && bookingList.isEmpty()) {
                Toast.makeText(this, "Không có booking để thanh toán!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isFromBooking && sessionList.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất 1 session để thanh toán!", Toast.LENGTH_SHORT).show();
                return;
            }

            showPaymentOptionsDialog();
        });
    }

    private void loadUnpaidSessions() {
        String username = getSharedPreferences("LoginDetails", MODE_PRIVATE).getString("Username", "");
        UsernameRequest request = new UsernameRequest(username);

        apiService.getUnpaidSessions(request).enqueue(new Callback<List<SessionResponse>>() {
            @Override
            public void onResponse(Call<List<SessionResponse>> call, Response<List<SessionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sessionList.clear();
                    sessionList.addAll(response.body());
                    sessionAdapter.notifyDataSetChanged();
                    updateTotalAmount();
                }
            }

            @Override
            public void onFailure(Call<List<SessionResponse>> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, "Lỗi kết nối server!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSingleBooking(long bookingId) {
        BookingRequest request = new BookingRequest();
        request.setId(bookingId);

        apiService.getBookingById(request).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bookingList.clear();
                    bookingList.add(response.body());
                    bookingAdapter.notifyDataSetChanged();
                    updateTotalAmount();
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, "Lỗi kết nối server!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateTotalAmount() {
        totalAmount = 0;

        if (isFromBooking && !bookingList.isEmpty()) {
            totalAmount = bookingList.get(0).getFee();
        } else {
            for (SessionResponse session : sessionList) {
                totalAmount += session.getFee();
            }
        }

        binding.txtTotal.setText("Tổng tiền: " + (int) totalAmount + " đ");
    }

    private void showPaymentOptionsDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_payment_options);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        Button btnBankTransfer = dialog.findViewById(R.id.btnBankTransfer);
        Button btnMomo = dialog.findViewById(R.id.btnMomo);
        Button btnZaloPay = dialog.findViewById(R.id.btnZaloPay);  // Thêm nút ZaloPay

        btnBankTransfer.setOnClickListener(v -> {
            dialog.dismiss();
            createBankPayment();
        });

        btnMomo.setOnClickListener(v -> {
            dialog.dismiss();
            createMomoPayment();
        });

        btnZaloPay.setOnClickListener(v -> {
            dialog.dismiss();
            createZaloPayPayment();
        });

        dialog.show();
    }


    private void createBankPayment() {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(String.valueOf((int) totalAmount));
        paymentRequest.setMethod("BANK_TRANSFER");

        apiService.createBankPayment(paymentRequest).enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                handlePaymentResponse(response, false);
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createMomoPayment() {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(String.valueOf((int) totalAmount));
        paymentRequest.setMethod("MOMO");

        apiService.createMomoPayment(paymentRequest).enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                handlePaymentResponse(response, true);
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handlePaymentResponse(Response<PaymentResponse> response, boolean isMomo) {
        if (response.isSuccessful() && response.body() != null) {
            PaymentResponse paymentResponse = response.body();

            Intent intent = new Intent(PaymentActivity.this, QRPaymentActivity.class);
            intent.putExtra("transactionId", paymentResponse.getTransactionId());
            intent.putExtra("amount", totalAmount);
            SharedPreferences prefs = getSharedPreferences(PREFS_PAYMENT, MODE_PRIVATE);
            prefs.edit().putString(KEY_TRANSACTION_ID, paymentResponse.getTransactionId()).apply();
            if (isFromBooking) {
                intent.putExtra("bookingId", bookingId);
                intent.putExtra("isFromBooking", true);
            } else {
                ArrayList<Long> selectedSessionIds = new ArrayList<>();
                for (SessionResponse session : sessionList) {
                    selectedSessionIds.add(session.getId());
                }
                intent.putExtra("selectedSessionIds", selectedSessionIds);
                intent.putExtra("isFromBooking", false);
            }

            startActivity(intent);
        }
    }


    private void openMomoApp(String payUrl) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(payUrl));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "MoMo chưa được cài đặt trên thiết bị", Toast.LENGTH_SHORT).show();
        }
    }

    private void createZaloPayPayment() {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(String.valueOf((int) totalAmount));
        paymentRequest.setMethod("ZALOPAY");  // hoặc "ZALO_PAY", tùy backend bạn nhận thế nào

        apiService.createZaloPayPayment(paymentRequest).enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String payUrl = response.body().getPayUrl();
                    if (payUrl != null && !payUrl.isEmpty()) {
                        openZaloPayApp(payUrl);
                    } else {
                        Toast.makeText(PaymentActivity.this, "Không nhận được đường link thanh toán ZaloPay", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PaymentActivity.this, "Thanh toán ZaloPay thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, "Lỗi mạng khi tạo giao dịch ZaloPay", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openZaloPayApp(String payUrl) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(payUrl));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "ZaloPay chưa được cài đặt trên thiết bị hoặc không thể mở", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendZaloPayNotify(String transactionId) {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setTransactionId(transactionId);

        apiService.confirmZaloPayment(paymentRequest).enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PaymentActivity.this, "Đã gửi notify tới server", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PaymentActivity.this, "Lỗi khi gửi notify", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, "Lỗi kết nối khi gửi notify", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
