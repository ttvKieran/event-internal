package com.example.payment_service.presentation.rest;

import com.example.payment_service.application.dto.command.VnPayIpnCommand;
import com.example.payment_service.application.port.in.PaymentUseCase;
import com.example.payment_service.application.port.out.PaymentGatewayPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentUseCase paymentUseCase;
    private final PaymentGatewayPort paymentGatewayPort;

    /**
     * Nhận kết quả từ VNPay khi trình duyệt khách hàng được redirect về.
     * Thực hiện nghiệp vụ (Cập nhật DB, bắn Kafka) (Bỏ qua IPN vì chưa cấu hình được ipn).
     */
    @GetMapping("/vnpay-return")
    public ResponseEntity<Map<String, String>> vnpayReturn(@RequestParam Map<String, String> allParams) {
        log.info("[Payment] Nhận Return URL từ VNPay: {}", allParams);
        try {
            Map<String, String> vnpParams = new HashMap<>();
            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                if (entry.getKey().startsWith("vnp_")) {
                    vnpParams.put(entry.getKey(), entry.getValue());
                }
            }
            if (!paymentGatewayPort.verifyIpnSignature(vnpParams)) {
                log.warn("[Payment] VNPay Return có chữ ký KHÔNG hợp lệ!");
                return ResponseEntity.badRequest().body(Map.of("message", "Chữ ký không hợp lệ"));
            }
            String vnpTxnRef = vnpParams.get("vnp_TxnRef");
            String vnpTransactionNo = vnpParams.get("vnp_TransactionNo");
            String vnpResponseCode = vnpParams.get("vnp_ResponseCode");

            VnPayIpnCommand command = new VnPayIpnCommand(vnpTxnRef, vnpTransactionNo, vnpResponseCode);

            paymentUseCase.handleVnPayIpn(command);

            if ("00".equals(vnpResponseCode)) {
                return ResponseEntity.ok(Map.of("message", "Thanh toán VNPay thành công!", "orderId", vnpTxnRef));
            } else {
                return ResponseEntity.ok(Map.of("message", "Thanh toán VNPay thất bại hoặc bị huỷ.", "orderId", vnpTxnRef));
            }

        } catch (Exception e) {
            log.error("[Payment] Lỗi khi xử lý VNPay Return: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("message", "Lỗi hệ thống"));
        }
    }
}
