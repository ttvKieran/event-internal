package com.example.attendance_service.domain.model.aggregate;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class QRSession {
    private String id;
    private String eventId;
    private List<String> qrCodes;
    private Instant expiresAt;

    public boolean isValidToken(String tokenToTest) {
        if (Instant.now().isAfter(this.expiresAt)) {
            return false;
        }
        return qrCodes.contains(tokenToTest);
    }
}
