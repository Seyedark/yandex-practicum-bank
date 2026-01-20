package ru.yandex.practicum.cash.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.cash.dto.ChangeAccountBalanceFrontRequestDto;

@Component
@RequiredArgsConstructor
public class MetricService {
    private final MeterRegistry registry;

    public void failedChangeAccountBalance(ChangeAccountBalanceFrontRequestDto changeAccountBalanceFrontRequestDto) {
        Counter.builder("failed_change_account_balance")
                .tag("login", changeAccountBalanceFrontRequestDto.getLogin())
                .tag("service", "cash-service")
                .tag("status", "failure")
                .description("Failed change account balance operation")
                .register(registry)
                .increment();
    }
}