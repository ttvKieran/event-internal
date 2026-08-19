package com.example.payment_service.application.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VnPayIpnCommand {
    private String vnpTxnRef;        // registrationId (Mã đơn hàng bên mình gửi cho VNPay)
    private String vnpTransactionNo; // Mã giao dịch do VNPay cấp (lưu vào providerTxnId)
    private String vnpResponseCode;  // "00" = Thành công, còn lại = Thất bại
}
