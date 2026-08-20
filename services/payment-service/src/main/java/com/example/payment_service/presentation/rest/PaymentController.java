package com.example.payment_service.presentation.rest;

import com.example.payment_service.application.dto.command.VnPayIpnCommand;
import com.example.payment_service.application.port.in.PaymentUseCase;
import com.example.payment_service.application.port.out.PaymentGatewayPort;
import com.example.payment_service.domain.model.aggregate.PaymentTransaction;
import com.example.payment_service.presentation.dto.response.ApiResponse;
import com.example.payment_service.presentation.dto.response.PaymentResponseDTO;
import com.example.payment_service.presentation.mapper.PaymentApiMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentUseCase paymentUseCase;
    private final PaymentGatewayPort paymentGatewayPort;
    private final PaymentApiMapper paymentMapper;
    @Value("${payment.frontend-result-url:http://localhost:5173/payment/result}")
    private String frontendResultUrl;

    /**
     * Nhận kết quả từ VNPay khi trình duyệt khách hàng được redirect về.
     * Thực hiện nghiệp vụ (Cập nhật DB, bắn Kafka) (Bỏ qua IPN vì chưa cấu hình được ipn).
     */
    @GetMapping("/vnpay-return")
    public ResponseEntity<Void> vnpayReturn(@RequestParam Map<String, String> allParams) {
        log.info("[Payment] Nhận Return URL từ VNPay: {}", allParams);
        String vnpTxnRef = allParams.get("vnp_TxnRef");
        String vnpResponseCode = allParams.get("vnp_ResponseCode");
        try {
            Map<String, String> vnpParams = new HashMap<>();
            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                if (entry.getKey().startsWith("vnp_")) {
                    vnpParams.put(entry.getKey(), entry.getValue());
                }
            }
            if (paymentGatewayPort.verifyIpnSignature(vnpParams)) {
                VnPayIpnCommand command = new VnPayIpnCommand(vnpTxnRef, vnpParams.get("vnp_TransactionNo"), vnpResponseCode);
                paymentUseCase.handleVnPayIpn(command);
            } else {
                log.warn("[Payment] VNPay Return có chữ ký KHÔNG hợp lệ!");
            }
        } catch (Exception e) {
            log.error("[Payment] Lỗi khi xử lý VNPay Return: {}", e.getMessage(), e);
        }
        // Tạo Redirect
        String redirectUrl = String.format("%s?vnp_ResponseCode=%s&vnp_TxnRef=%s",
            frontendResultUrl,
            vnpResponseCode != null ? vnpResponseCode : "99",
            vnpTxnRef != null ? vnpTxnRef : "");
        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create(redirectUrl))
            .build();
    }

    @GetMapping("/{registrationId}")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> getPaymentByRegistrationId(@PathVariable UUID registrationId) {
        PaymentTransaction transaction = paymentUseCase.getPaymentByRegistrationId(registrationId);
        PaymentResponseDTO responseDto = paymentMapper.toResponseDto(transaction);
        return ResponseEntity.ok(ApiResponse.ok(responseDto));
    }
}
