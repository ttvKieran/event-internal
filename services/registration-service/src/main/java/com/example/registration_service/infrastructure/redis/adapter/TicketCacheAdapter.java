package com.example.registration_service.infrastructure.redis.adapter;

import com.example.registration_service.application.port.out.TicketCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TicketCacheAdapter implements TicketCachePort {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void initializeTicketCache(UUID campaignId, int totalTickets) {
        String key = "campaign:tickets:" + campaignId;
        redisTemplate.opsForValue().set(key, String.valueOf(totalTickets));
    }

    @Override
    public boolean decrementTicket(UUID campaignId) {
        String key = "campaign:tickets:" + campaignId;
        Long remainingTickets = redisTemplate.opsForValue().decrement(key);
        if (remainingTickets != null && remainingTickets >= 0) {
            return true;
        } else {
            redisTemplate.opsForValue().increment(key);
            return false;
        }
    }

    @Override
    public void setReservationStatus(UUID campaignId, UUID userId, String status) {
        String key = "ticket_status:" + campaignId + ":" + userId;
        redisTemplate.opsForValue().set(key, status, Duration.ofMinutes(10));
    }

    @Override
    public String getReservationStatus(UUID campaignId, UUID userId) {
        String key = "ticket_status:" + campaignId + ":" + userId;
        return redisTemplate.opsForValue().get(key);
    }
}
