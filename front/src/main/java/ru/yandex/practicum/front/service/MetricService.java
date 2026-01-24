package ru.yandex.practicum.front.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MetricService {
    private final MeterRegistry registry;

    public void successfulLogin(String login) {
        registry.counter("success_login",
                "login", login,
                "service", "front-service",
                "status", "success"
        ).increment();
    }

    public void failedLogin(String login) {
        registry.counter("failed_login",
                "login", login,
                "service", "front-service",
                "status", "failure"
        ).increment();
    }
}