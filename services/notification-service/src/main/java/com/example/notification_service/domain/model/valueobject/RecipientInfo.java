package com.example.notification_service.domain.model.valueobject;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecipientInfo {
    String email;
    String fullName;

    public static RecipientInfo of(String email, String fullName) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email người nhận không hợp lệ: " + email);
        }
        return new RecipientInfo(email, fullName != null ? fullName : "Bạn");
    }
}
