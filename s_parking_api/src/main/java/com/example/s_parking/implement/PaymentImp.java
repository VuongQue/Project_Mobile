package com.example.s_parking.implement;

import com.example.s_parking.dto.request.ConfirmPaymentRequest;
import com.example.s_parking.dto.request.PaymentRequest;
import com.example.s_parking.dto.response.PaymentResponse;
import com.example.s_parking.entity.Payment;
import com.example.s_parking.entity.Session;
import com.example.s_parking.repository.PaymentRepository;
import com.example.s_parking.repository.SessionRepository;
import com.example.s_parking.service.PaymentService;
import com.example.s_parking.value.PaymentStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentImp implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final SessionRepository sessionRepository;
    private static final Logger logger = LoggerFactory.getLogger(PaymentImp.class);

    @Value("${momo.endpoint}")
    private String momoEndpoint;

    @Value("${momo.partnerCode}")
    private String partnerCode;

    @Value("${momo.accessKey}")
    private String accessKey;

    @Value("${momo.secretKey}")
    private String secretKey;

    @Value("${momo.notifyUrl}")
    private String notifyUrl;

    @Value("${momo.redirectUrl}")
    private String redirectUrl;

    @Override
    public PaymentResponse createPayment(PaymentRequest request, String username) {
        Payment payment = new Payment();
        payment.setAmount(Double.parseDouble(request.getAmount()));
        payment.setMethod(request.getMethod());
        payment.setStatus(PaymentStatus.UNPAID);
        payment.setTransactionId(generateTransactionId());
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        return new PaymentResponse(payment.getTransactionId());
    }

    @Override
    public PaymentResponse createMomoPayment(PaymentRequest request, String username) {
        try {
            String requestId = UUID.randomUUID().toString();
            String transactionId = generateTransactionId();

            if (request.getOrderInfo() == null || request.getOrderInfo().isEmpty()) {
                throw new RuntimeException("OrderInfo không được để trống");
            }

            // Cập nhật rawData theo thứ tự chính xác của MoMo
            String rawData = "accessKey=" + accessKey +
                    "&amount=" + request.getAmount() +
                    "&extraData=" + "" +
                    "&ipnUrl=" + notifyUrl +
                    "&orderId=" + transactionId +
                    "&orderInfo=" + request.getOrderInfo() +
                    "&partnerCode=" + partnerCode +
                    "&redirectUrl=" + redirectUrl +
                    "&requestId=" + requestId +
                    "&requestType=captureWallet";

            logger.info("Raw Data for Signature: {}", rawData);

            String signature = generateSignature(rawData);
            logger.info("Generated Signature: {}", signature);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("partnerCode", partnerCode);
            requestBody.put("accessKey", accessKey);
            requestBody.put("requestId", requestId);
            requestBody.put("amount", String.valueOf(request.getAmount()));
            requestBody.put("orderId", transactionId);
            requestBody.put("orderInfo", request.getOrderInfo());
            requestBody.put("redirectUrl", redirectUrl);
            requestBody.put("ipnUrl", notifyUrl);
            requestBody.put("extraData", "");
            requestBody.put("requestType", "captureWallet");
            requestBody.put("signature", signature);

            ObjectMapper mapper = new ObjectMapper();
            String jsonRequest = mapper.writeValueAsString(requestBody);
            logger.info("MoMo Request Body: {}", jsonRequest);

            HttpPost post = new HttpPost(momoEndpoint);
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(jsonRequest, StandardCharsets.UTF_8));

            try (CloseableHttpClient client = HttpClients.createDefault();
                 CloseableHttpResponse response = client.execute(post)) {

                String responseBody = EntityUtils.toString(response.getEntity());
                logger.info("MoMo Response: {}", responseBody);

                Map<String, Object> responseMap = mapper.readValue(responseBody, HashMap.class);

                int resultCode = (int) responseMap.getOrDefault("resultCode", -1);
                String message = (String) responseMap.getOrDefault("message", "Unknown error");

                if (resultCode != 0) {
                    throw new RuntimeException("MoMo Error: " + message);
                }

                String payUrl = responseMap.get("payUrl") != null ? responseMap.get("payUrl").toString() : null;

                Payment payment = new Payment();
                payment.setAmount(Double.parseDouble(request.getAmount()));
                payment.setMethod("MoMo");
                payment.setStatus(PaymentStatus.UNPAID);
                payment.setTransactionId(transactionId);
                payment.setCreatedAt(LocalDateTime.now());

                paymentRepository.save(payment);

                return PaymentResponse.builder()
                        .transactionId(transactionId)
                        .amount(Double.parseDouble(request.getAmount()))
                        .method("MoMo")
                        .status(PaymentStatus.UNPAID.toString())
                        .createdAt(LocalDateTime.now())
                        .payUrl(payUrl)
                        .build();
            }
        } catch (Exception e) {
            logger.error("Error creating MoMo transaction: " + e.getMessage());
            throw new RuntimeException("Lỗi khi tạo giao dịch MoMo: " + e.getMessage());
        }
    }


    @Override
    public String confirmPayment(ConfirmPaymentRequest request, String username) {
        Optional<Payment> optionalPayment = paymentRepository.findByTransactionId(request.getTransactionId());
        if (optionalPayment.isEmpty()) {
            throw new RuntimeException("Không tìm thấy giao dịch.");
        }

        Payment payment = optionalPayment.get();

        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Giao dịch này đã được thanh toán trước đó.");
        }

        List<Session> sessions = sessionRepository.findAllById(request.getSessionIds());
        boolean isUserOwner = sessions.stream()
                .allMatch(session -> session.getUser().getUsername().equals(username));

        if (!isUserOwner) {
            throw new RuntimeException("Bạn không có quyền xác nhận giao dịch.");
        }

        for (Session session : sessions) {
            session.setPayment(payment);
        }
        sessionRepository.saveAll(sessions);

        payment.setStatus(PaymentStatus.PAID);
        paymentRepository.save(payment);

        return "Đã xác nhận thanh toán thành công.";
    }

    @Override
    public String updatePaymentStatus(String transactionId, String status) {
        Optional<Payment> optionalPayment = paymentRepository.findByTransactionId(transactionId);

        if (optionalPayment.isEmpty()) {
            return "Giao dịch không tồn tại!";
        }

        Payment payment = optionalPayment.get();

        if (payment.getStatus().equals(PaymentStatus.PAID.toString())) {
            return "Giao dịch đã được thanh toán trước đó!";
        }

        // Cập nhật trạng thái thanh toán
        payment.setStatus(PaymentStatus.valueOf(status));

        paymentRepository.save(payment);

        return "Trạng thái thanh toán đã được cập nhật thành công!";
    }


    private String generateSignature(String data) throws Exception {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSHA256.init(secretKeySpec);
        byte[] hash = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private String generateTransactionId() {
        return "TRANS_" + UUID.randomUUID().toString().replace("-", "").substring(0, 15).toUpperCase();
    }
}