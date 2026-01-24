package ru.yandex.practicum.cash.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.cash.dto.ChangeAccountBalanceFrontRequestDto;

@Component
@RequiredArgsConstructor
public class MetricService {
    private final MeterRegistry registry;

    public void failedChangeAccountBalance(ChangeAccountBalanceFrontRequestDto changeAccountBalanceFrontRequestDto) {
        registry.counter("failed_change_account_balance",
                "login", changeAccountBalanceFrontRequestDto.getLogin(),
                "service", "cash-service",
                "status", "failure"
        ).increment();
    }
}