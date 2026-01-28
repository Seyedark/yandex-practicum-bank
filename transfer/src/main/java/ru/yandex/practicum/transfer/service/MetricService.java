package ru.yandex.practicum.transfer.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.transfer.dto.TransferFrontRequestDto;

@Component
@RequiredArgsConstructor
public class MetricService {
    private final MeterRegistry registry;

    public void failedTransfer(TransferFrontRequestDto transferFrontRequestDto) {
        registry.counter("failed_transfer",
                "login_from", transferFrontRequestDto.getLoginFrom(),
                "login_to", transferFrontRequestDto.getLoginTo(),
                "service", "transfer-service",
                "status", "failure"
        ).increment();
    }
}