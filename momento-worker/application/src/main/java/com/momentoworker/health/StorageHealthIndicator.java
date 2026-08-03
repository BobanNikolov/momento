package com.momentoworker.health;

import com.momentoworker.service.integration.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StorageHealthIndicator implements HealthIndicator {

    private final StorageService storageService;

    @Override
    public Health health() {
        try {
            boolean healthy = storageService.isHealthy();
            return healthy ? Health.up().build() : Health.down().withDetail("status", "Storage unreachable").build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
