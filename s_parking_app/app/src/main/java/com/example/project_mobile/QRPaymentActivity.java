package com.example.project_mobile;

import android.content.Context;
import android.content.Intent;
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
import com.example.project_mobile.dto.ConfirmPaymentRequest;
import com.example.project_mobile.dto.SuccessResponse;
import com.example.project_mobile.dto.VietQRResponse;
import com.example.project_mobile.utils.LocalHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private Long bookingId; // Chỉ có một bookingId
    private ArrayList<Long> selectedSessionIds; // Có thể là nhiều session

    private ApiService apiService;

    private static final String ACCOUNT_NUMBER = "0397405880";
    private static final String BANK_BIN = "970422";
    private static final String ACCOUNT_NAME = "VUONG LAP QUE";
    private static final String DESCRIPTION = "THANH TOAN PHI GUI XE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_payment);

        imgQrCode = findViewById(R.id.imgQrCode);
        txtTransactionInfo = findViewById(R.id.txtTransactionInfo);
        btnConfirmTransfer = findViewById(R.id.btnConfirmTransfer);

        transactionId = getIntent().getStringExtra("transactionId");
        amount = getIntent().getDoubleExtra("amount", 0);
        bookingId = getIntent().getLongExtra("bookingId", -1); // Mặc định -1 nếu không có bookingId
        selectedSessionIds = (ArrayList<Long>) getIntent().getSerializableExtra("selectedSessionIds");

        apiService = ApiClient.getInstance(this);

        txtTransactionInfo.setText("Mã giao dịch: " + transactionId);

        generateVietQR();

        btnConfirmTransfer.setOnClickListener(v -> confirmPayment());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalHelper.setLocale(newBase));
    }

    private void generateVietQR() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.vietqr.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        VietQRService vietQRService = retrofit.create(VietQRService.class);

        Map<String, Object> body = new HashMap<>();
        body.put("accountNo", ACCOUNT_NUMBER);
        body.put("accountName", ACCOUNT_NAME);
        body.put("acqId", BANK_BIN);
        body.put("amount", (int) amount);
        body.put("addInfo", DESCRIPTION);
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
        String transactionId = getIntent().getStringExtra("transactionId");
        boolean isFromBooking = getIntent().getBooleanExtra("isFromBooking", false);
        Long bookingId = getIntent().getLongExtra("bookingId", -1);
        ArrayList<Long> selectedSessionIds = (ArrayList<Long>) getIntent().getSerializableExtra("selectedSessionIds");

        ConfirmPaymentRequest request;

        if (isFromBooking) {
            request = new ConfirmPaymentRequest(transactionId, bookingId, null);
        } else {
            request = new ConfirmPaymentRequest(transactionId, null, selectedSessionIds);
        }

        apiService.confirmPayment(request).enqueue(new Callback<SuccessResponse>() {
            @Override
            public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    Toast.makeText(QRPaymentActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(QRPaymentActivity.this, HistoryActivity.class);
                    startActivity(intent);
                    finishAffinity();
                } else {
                    Toast.makeText(QRPaymentActivity.this, "Xác nhận thanh toán thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SuccessResponse> call, Throwable t) {
                Toast.makeText(QRPaymentActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
