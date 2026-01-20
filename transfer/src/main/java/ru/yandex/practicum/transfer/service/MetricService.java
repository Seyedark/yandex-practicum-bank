package ru.yandex.practicum.transfer.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.transfer.dto.TransferFrontRequestDto;

@Component
@RequiredArgsConstructor
public class MetricService {
    private final MeterRegistry registry;

    public void failedTransfer(TransferFrontRequestDto transferFrontRequestDto) {
        Counter.builder("failed_transfer")
                .tag("login_from", transferFrontRequestDto.getLoginFrom())
                .tag("login_to", transferFrontRequestDto.getLoginTo())
                .tag("service", "transfer-service")
                .tag("status", "failure")
                .description("Failed transfer operation")
                .register(registry)
                .increment();
    }
}