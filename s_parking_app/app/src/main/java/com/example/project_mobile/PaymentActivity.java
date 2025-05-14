package com.example.project_mobile;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.project_mobile.adapter.SessionSelectAdapter;
import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.databinding.ActivityPaymentBinding;
import com.example.project_mobile.dto.PaymentRequest;
import com.example.project_mobile.dto.PaymentResponse;
import com.example.project_mobile.dto.SessionResponse;
import com.example.project_mobile.dto.UsernameRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {

    private ActivityPaymentBinding binding;
    private SessionSelectAdapter adapter;
    private final List<SessionResponse> sessionList = new ArrayList<>();
    private final List<SessionResponse> selectedSessions = new ArrayList<>();
    private double totalAmount = 0.0;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = ApiClient.getInstance(getApplicationContext());

        setupRecyclerView();
        setupEvents();
        loadDebtSessions();
    }

    private void setupRecyclerView() {
        adapter = new SessionSelectAdapter(sessionList, selectedSessionsChanged -> {
            selectedSessions.clear();
            selectedSessions.addAll(selectedSessionsChanged);
            updateTotalAmount();
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupEvents() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.cbAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            adapter.selectAll(isChecked);
        });

        binding.btnPay.setOnClickListener(v -> {
            if (selectedSessions.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất 1 lượt gửi xe để thanh toán!", Toast.LENGTH_SHORT).show();
            } else {
                showPaymentOptionsDialog();
            }
        });
    }

    private void loadDebtSessions() {
        String username = getSharedPreferences("LoginDetails", MODE_PRIVATE)
                .getString("Username", "");

        apiService.getUnpaidSessions(new UsernameRequest(username)).enqueue(new Callback<List<SessionResponse>>() {
            @Override
            public void onResponse(Call<List<SessionResponse>> call, Response<List<SessionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sessionList.clear();
                    sessionList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(PaymentActivity.this, "Không tìm thấy khoản nợ nào!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SessionResponse>> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, "Lỗi kết nối server!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotalAmount() {
        totalAmount = 0;
        for (SessionResponse session : selectedSessions) {
            totalAmount += session.getFee();
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

        btnBankTransfer.setOnClickListener(v -> {
            dialog.dismiss();
            createPayment("BANK_TRANSFER");
        });

        btnMomo.setOnClickListener(v -> {
            dialog.dismiss();
            createPayment("MOMO");
        });

        dialog.show();
    }

    private void createPayment(String method) {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(totalAmount);
        paymentRequest.setMethod(method);

        apiService.createPayment(paymentRequest).enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String transactionId = response.body().getTransactionId();
                    if (transactionId == null || transactionId.isEmpty()) {
                        Toast.makeText(PaymentActivity.this, "Không nhận được mã giao dịch!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (method.equals("MOMO")) {
                        openMomoApp(response.body().getPayUrl());
                    } else {
                        Intent intent = new Intent(PaymentActivity.this, QRPaymentActivity.class);
                        intent.putExtra("transactionId", transactionId);
                        intent.putExtra("amount", totalAmount);
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(PaymentActivity.this, "Tạo giao dịch thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openMomoApp(String payUrl) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(payUrl));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "MoMo chưa được cài đặt trên thiết bị", Toast.LENGTH_SHORT).show();
        }
    }
}
