package com.inventory.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ConfigServerHealthIndicator implements HealthIndicator {

    @Value("${spring.cloud.config.uri:http://localhost:8888}")
    private String configServerUri;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Health health() {
        try {
            restTemplate.getForEntity(configServerUri + "/actuator/health", String.class);
            return Health.up().withDetail("configServer", "connected").build();
        } catch (Exception e) {
            return Health.down().withDetail("configServer", "disconnected").build();
        }
    }

    public Map<String, Object> getHealthStatus() {
        Map<String, Object> result = new LinkedHashMap<>();
        Health health = health();
        result.put("status", health.getStatus().toString());
        result.put("configServer", health.getDetails().getOrDefault("configServer", "unknown"));
        return result;
    }
}
