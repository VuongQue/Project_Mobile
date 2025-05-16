package com.example.s_parking.implement;

import com.example.s_parking.dto.request.ConfirmPaymentRequest;
import com.example.s_parking.dto.request.PaymentRequest;
import com.example.s_parking.dto.response.PaymentResponse;
import com.example.s_parking.entity.Booking;
import com.example.s_parking.entity.ParkingLot;
import com.example.s_parking.entity.Payment;
import com.example.s_parking.entity.Session;
import com.example.s_parking.repository.BookingRepository;
import com.example.s_parking.repository.ParkingLotRepository;
import com.example.s_parking.repository.PaymentRepository;
import com.example.s_parking.repository.SessionRepository;
import com.example.s_parking.service.PaymentService;
import com.example.s_parking.value.ParkingStatus;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentImp implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final SessionRepository sessionRepository;
    private final BookingRepository bookingRepository;
    private final ParkingLotRepository parkingLotRepository;


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

    @Value("${zalopay.appId}")
    private int zaloAppId;

    @Value("${zalopay.key1}")
    private String zaloKey1;

    @Value("${zalopay.key2}")
    private String zaloKey2;

    @Value("${zalopay.endpoint}")
    private String zaloEndpoint;

    @Value("${zalopay.redirectUrl}")
    private String zaloRedirectUrl;

    @Value("${zalopay.callbackUrl}")
    private String zaloCallbackUrl;

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

        // Thanh toán cho Booking (chỉ 1 Booking)
        if (request.getBookingId() != null) {
            Optional<Booking> optionalBooking = bookingRepository.findById(request.getBookingId());

            if (optionalBooking.isPresent()) {
                Booking booking = optionalBooking.get();

                // Kiểm tra thời gian hết hạn
                LocalDateTime expiryTime = booking.getCreatedAt().plusMinutes(10);
                if (LocalDateTime.now().isAfter(expiryTime)) {
                    // Hủy booking và cập nhật lại chỗ đậu xe
                    ParkingLot parkingLot = booking.getParking();
                    if (parkingLot != null) {
                        parkingLot.setStatus(ParkingStatus.AVAILABLE);
                        parkingLotRepository.save(parkingLot);
                    }
                    bookingRepository.delete(booking);
                    return "Booking đã quá thời hạn thanh toán và đã bị hủy!";
                }

                // Cập nhật payment
                booking.setPayment(payment);
                bookingRepository.save(booking);

                // Cập nhật trạng thái chỗ đậu xe
                ParkingLot parkingLot = booking.getParking();
                if (parkingLot != null) {
                    parkingLot.setStatus(ParkingStatus.RESERVED);
                    parkingLotRepository.save(parkingLot);
                }
            } else {
                throw new RuntimeException("Không tìm thấy booking.");
            }
        }

        // Thanh toán cho Session (nhiều Session)
        if (request.getSessionIds() != null && !request.getSessionIds().isEmpty()) {
            List<Session> sessions = sessionRepository.findAllById(request.getSessionIds());

            for (Session session : sessions) {
                session.setPayment(payment);
            }
            sessionRepository.saveAll(sessions);
        }

        // Cập nhật trạng thái payment
        payment.setStatus(PaymentStatus.PAID);
        paymentRepository.save(payment);

        return "Thanh toán thành công!";
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

    @Override
    public PaymentResponse createZaloPayPayment(PaymentRequest request, String username) {
        try {
            String appUser = username;
            String appTransId = generateZaloTransactionId(); // sinh mã chuẩn yyMMdd_xxxxxx
            long amount = Long.parseLong(request.getAmount());

            String embedDataString = "{}";
            String item = "[]";

            // Lấy thời gian hiện tại dạng timestamp milliseconds
            long appTime = System.currentTimeMillis();

            // Chuỗi raw data đúng chuẩn theo tài liệu ZaloPay
            String rawData = zaloAppId + "|" + appTransId + "|" + appUser + "|" + amount + "|" + appTime + "|" + embedDataString + "|" + item;

            // Tạo chữ ký (mac) với key1
            String mac = generateZaloSignature(rawData, zaloKey1);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("app_id", zaloAppId);
            requestBody.put("app_trans_id", appTransId);
            requestBody.put("app_user", appUser);
            requestBody.put("amount", amount);
            requestBody.put("app_time", appTime);
            requestBody.put("embed_data", embedDataString);
            requestBody.put("item", item);
            requestBody.put("description", "Thanh toán đơn hàng " + appTransId);
            requestBody.put("bank_code", "zalopayapp");
            requestBody.put("callback_url", zaloCallbackUrl);
            requestBody.put("redirect_url", "myapp://payment_result");
            requestBody.put("mac", mac);

            ObjectMapper mapper = new ObjectMapper();
            String jsonRequest = mapper.writeValueAsString(requestBody);

            HttpPost post = new HttpPost(zaloEndpoint);
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(jsonRequest, StandardCharsets.UTF_8));
            logger.info("ZaloPay Request JSON: {}", jsonRequest);

            try (CloseableHttpClient client = HttpClients.createDefault();
                 CloseableHttpResponse response = client.execute(post)) {

                String responseBody = EntityUtils.toString(response.getEntity());
                logger.info("ZaloPay Response Body: {}", responseBody);

                Map<String, Object> responseMap = mapper.readValue(responseBody, HashMap.class);

                int returnCode = (int) responseMap.getOrDefault("return_code", -1);
                String returnMessage = (String) responseMap.getOrDefault("return_message", "Error");

                logger.info("ZaloPay return_code: {}, return_message: {}", returnCode, returnMessage);

                if (returnCode != 1) {
                    throw new RuntimeException("ZaloPay Error: " + returnMessage);
                }

                String orderUrl = (String) responseMap.get("order_url");
                String orderToken = (String) responseMap.get("order_token");
                logger.info("Order URL: {}", orderUrl);

                Payment payment = new Payment();
                payment.setAmount(amount);
                payment.setMethod("ZaloPay");
                payment.setStatus(PaymentStatus.UNPAID);
                payment.setTransactionId(appTransId);
                payment.setCreatedAt(LocalDateTime.now());

                paymentRepository.save(payment);



                return PaymentResponse.builder()
                        .transactionId(appTransId)
                        .amount((double) amount)
                        .method("ZaloPay")
                        .status(PaymentStatus.UNPAID.toString())
                        .createdAt(LocalDateTime.now())
                        .payUrl(orderUrl)
                        .orderToken(orderToken)
                        .build();
            }
        } catch (Exception e) {
            logger.error("Error creating ZaloPay transaction", e);
            throw new RuntimeException("Lỗi tạo đơn ZaloPay: " + e.getMessage(), e);
        }
    }

    private String generateZaloTransactionId() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        return datePart + "_" + randomPart;
    }



    private String generateZaloSignature(String data, String key) throws Exception {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSHA256.init(secretKeySpec);
        byte[] hash = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}