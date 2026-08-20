package com.example.payment_service.presentation.rest;

import com.example.payment_service.application.dto.command.VnPayIpnCommand;
import com.example.payment_service.application.port.in.PaymentUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentUseCase paymentUseCase;
    /**
     * VNPay gọi ngầm vào đây sau khi xử lý thanh toán (IPN — Instant Payment Notification).
     * User không thấy endpoint này, chỉ VNPay server gọi trực tiếp
     * VNPay quy định phải trả về JSON: {"RspCode":"00","Message":"Confirm Success"} nếu xử lý thành công, các mã lỗi khác nếu thất bại.
     */
    @GetMapping("/vnpay-ipn")
    public ResponseEntity<Map<String, String>> vnPayIpn(
        @RequestParam("vnp_TxnRef")       String vnpTxnRef,        // = registrationId
        @RequestParam("vnp_TransactionNo") String vnpTransactionNo, // Mã giao dịch VNPay
        @RequestParam("vnp_ResponseCode")  String vnpResponseCode,  // "00" = OK
        @RequestParam("vnp_SecureHash")    String vnpSecureHash     // Chữ ký bảo mật
    ) {
        log.info("[Payment] Nhận IPN từ VNPay: txnRef={}, responseCode={}", vnpTxnRef, vnpResponseCode);

        try {
            // TODO: Xác thực chữ ký vnpSecureHash trước khi xử lý
            // if (!vnPayService.validateSignature(request)) return badSignature();

            VnPayIpnCommand command = new VnPayIpnCommand(vnpTxnRef, vnpTransactionNo, vnpResponseCode);
            paymentUseCase.handleVnPayIpn(command);

            // VNPay yêu cầu trả về theo format
            return ResponseEntity.ok(Map.of(
                "RspCode", "00",
                "Message", "Confirm Success"
            ));

        } catch (Exception e) {
            log.error("[Payment] Lỗi xử lý IPN VNPay: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of(
                "RspCode", "99",
                "Message", "Internal Error"
            ));
        }
    }
}
