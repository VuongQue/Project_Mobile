package com.example.project_mobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.api.VietQRService;
import com.example.project_mobile.dto.PaymentRequest;
import com.example.project_mobile.dto.VietQRResponse;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QRPaymentActivity extends AppCompatActivity {

    private ImageView imgQrCode;
    private TextView txtTransactionInfo;
    private Button btnConfirmTransfer;

    private String transactionId;
    private double amount;
    private ApiService apiService;

    private static final String ACCOUNT_NUMBER = "0397405880"; // MB Bank
    private static final String BANK_BIN = "970422"; // BIN code MB
    private static final String DESCRIPTION = "THANH TOAN PHI GUI XE"; // Nội dung cố định

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_payment);

        imgQrCode = findViewById(R.id.imgQrCode);
        txtTransactionInfo = findViewById(R.id.txtTransactionInfo);
        btnConfirmTransfer = findViewById(R.id.btnConfirmTransfer);

        transactionId = getIntent().getStringExtra("transactionId");
        amount = getIntent().getDoubleExtra("amount", 0);

        apiService = ApiClient.getInstance(this);

        txtTransactionInfo.setText("Mã giao dịch: " + transactionId);

        generateVietQR();

        btnConfirmTransfer.setOnClickListener(v -> confirmPayment());
    }

    private void generateVietQR() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.vietqr.io/") // Base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        VietQRService vietQRService = retrofit.create(VietQRService.class);

        Map<String, Object> body = new HashMap<>();
        body.put("accountNo", "0397405880");
        body.put("accountName", "NGUYEN VAN A"); // Anh thay tên chủ tài khoản thật
        body.put("acqId", "970422"); // Mã MB Bank
        body.put("amount", (int) amount); // Chuyển double về int
        body.put("addInfo", "THANH TOAN PHI GUI XE");
        body.put("template", "compact");

        vietQRService.generateQR(body).enqueue(new Callback<VietQRResponse>() {
            @Override
            public void onResponse(Call<VietQRResponse> call, Response<VietQRResponse> response) {
                if (response.isSuccessful() && response.body() != null && "00".equals(response.body().getCode())) {
                    String qrUrl = response.body().getData().getQrDataURL();
                    Glide.with(QRPaymentActivity.this).load(qrUrl).into(imgQrCode);
                } else {
                    Toast.makeText(QRPaymentActivity.this, "Không lấy được QR Code!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VietQRResponse> call, Throwable t) {
                Toast.makeText(QRPaymentActivity.this, "Lỗi mạng khi tạo QR!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void confirmPayment() {
        PaymentRequest request = new PaymentRequest();
        request.setTransactionId(transactionId);

        apiService.confirmPayment(request).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(QRPaymentActivity.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(QRPaymentActivity.this, HistoryActivity.class);
                    startActivity(intent);
                    finishAffinity();
                } else {
                    Toast.makeText(QRPaymentActivity.this, "Xác nhận thanh toán thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(QRPaymentActivity.this, "Lỗi mạng khi xác nhận!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
