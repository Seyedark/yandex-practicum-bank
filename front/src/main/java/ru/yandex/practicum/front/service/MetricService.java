package ru.yandex.practicum.front.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MetricService {
    private final MeterRegistry registry;

    public void successfulLogin(String login) {
        Counter.builder("success_login")
                .tag("status", "success")
                .tag("service", "front-service")
                .tag("login", login)
                .description("Successful user login attempt")
                .register(registry)
                .increment();
    }

    public void failedLogin(String login) {
        Counter.builder("failed_login")
                .tag("status", "failure")
                .tag("service", "front-service")
                .tag("login", login)
                .description("Failed user login attempt")
                .register(registry)
                .increment();
    }
}