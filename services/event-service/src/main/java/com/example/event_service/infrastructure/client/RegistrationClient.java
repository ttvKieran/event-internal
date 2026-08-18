package com.example.event_service.infrastructure.client;

import com.example.event_service.infrastructure.client.dto.CampaignStatsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

@FeignClient(name = "registration-service", url = "${services.registration.url}")
public interface RegistrationClient {

    @GetMapping("/api/v1/registrations/campaigns/{campaignId}/stats")
    @CircuitBreaker(name = "registrationCB", fallbackMethod = "fallbackStats")
    CampaignStatsResponse getStats(@PathVariable("campaignId") String campaignId);

    // Fallback
    default CampaignStatsResponse fallbackStats(String campaignId, Throwable t) {
        System.out.println("CẦU DAO ĐÃ NHẢY! Registration Service sập: " + t.getMessage());
        return new CampaignStatsResponse(-1);
    }
}
