package com.example.s_parking.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmPaymentRequest {
    private String transactionId;    // Mã giao dịch cần xác nhận
    private List<Long> sessionIds;    // Danh sách các sessionId mà người dùng đã chọn để thanh toán
}
