package com.example.payment_service.infrastructure.payment;

import com.example.payment_service.application.port.out.PaymentGatewayPort;
import com.example.payment_service.domain.model.valueobject.Money;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
public class VnPayGatewayAdapter implements PaymentGatewayPort {

     @Value("${vnpay.pay-url}")
    private String vnpayPayUrl;

     @Value("${vnpay.tmn-code}")
    private String tmnCode;

     @Value("${vnpay.hash-secret}")
    private String hashSecret;

     @Value("${vnpay.return-url}")
    private String returnUrl;

    @Override
    public String createPaymentUrl(UUID registrationId, Money amount, String orderInfo) {
        log.info("[VNPay] Tạo URL thanh toán cho registrationId={}, amount={}",
                registrationId, amount);
        long vnpAmount = amount.getAmount()
                .multiply(java.math.BigDecimal.valueOf(100))
                .longValue();
        String createDate = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());

        Map<String, String> vnpParams = new TreeMap<>(); // TreeMap tự sort theo alphabet
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", tmnCode);
        vnpParams.put("vnp_Amount", String.valueOf(vnpAmount));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", registrationId.toString()); // Mã đơn hàng bên mình
        vnpParams.put("vnp_OrderInfo", sanitizeOrderInfo(orderInfo));
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", returnUrl);
        vnpParams.put("vnp_IpAddr", "127.0.0.1"); // IP của server
        vnpParams.put("vnp_CreateDate", createDate);

        StringBuilder hashData = new StringBuilder();
        StringBuilder queryString = new StringBuilder();

        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value != null && !value.isEmpty()) {
                hashData.append(key).append('=')
                        .append(URLEncoder.encode(value, StandardCharsets.US_ASCII))
                        .append('&');
                queryString.append(URLEncoder.encode(key, StandardCharsets.US_ASCII))
                        .append('=')
                        .append(URLEncoder.encode(value, StandardCharsets.US_ASCII))
                        .append('&');
            }
        }

        hashData.deleteCharAt(hashData.length() - 1);
        queryString.deleteCharAt(queryString.length() - 1);

        // Tính chữ ký HMAC-SHA512
        String secureHash = hmacSHA512(hashSecret, hashData.toString());

        String paymentUrl = vnpayPayUrl + "?" + queryString + "&vnp_SecureHash=" + secureHash;

        log.info("[VNPay] Đã tạo payment URL thành công. registrationId={}", registrationId);
        return paymentUrl;
    }

    @Override
    public boolean verifyIpnSignature(Map<String, String> params) {
        String vnpSecureHash = params.get("vnp_SecureHash");
        if (vnpSecureHash == null || vnpSecureHash.isBlank()) {
            log.warn("[VNPay] IPN thiếu vnp_SecureHash!");
            return false;
        }

        Map<String, String> filteredParams = new TreeMap<>(params);
        filteredParams.remove("vnp_SecureHash");
        filteredParams.remove("vnp_SecureHashType");

        // Build lại chuỗi hash data từ các params còn lại (sort theo alphabet)
        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> entry : filteredParams.entrySet()) {
            String value = entry.getValue();
            if (value != null && !value.isEmpty()) {
                hashData.append(entry.getKey()).append('=')
                        .append(URLEncoder.encode(value, StandardCharsets.US_ASCII))
                        .append('&');
            }
        }
        hashData.deleteCharAt(hashData.length() - 1);

        String calculatedHash = hmacSHA512(hashSecret, hashData.toString());
        boolean isValid = calculatedHash.equalsIgnoreCase(vnpSecureHash);

        if (isValid) {
            log.info("[VNPay] Xác thực chữ ký IPN thành công.");
        } else {
            log.warn("[VNPay] Chữ ký IPN KHÔNG HỢP LỆ! Expected={}, Got={}",
                    calculatedHash, vnpSecureHash);
        }
        return isValid;
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();

        } catch (Exception e) {
            throw new RuntimeException("[VNPay] Lỗi tính HMAC-SHA512: " + e.getMessage(), e);
        }
    }

    private String sanitizeOrderInfo(String orderInfo) {
        if (orderInfo == null)
            return "Thanh toan ve su kien";
        return orderInfo.replaceAll("[^a-zA-Z0-9 ]", "");
    }
}
