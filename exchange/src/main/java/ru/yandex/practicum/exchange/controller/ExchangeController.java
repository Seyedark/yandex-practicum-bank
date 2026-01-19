package ru.yandex.practicum.exchange.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.exchange.dto.ConvertRequestDto;
import ru.yandex.practicum.exchange.dto.ConvertResponseDto;
import ru.yandex.practicum.exchange.dto.ExchangeDto;
import ru.yandex.practicum.exchange.service.ExchangeService;

import java.util.List;

@RestController
@RequestMapping("/exchange")
@RequiredArgsConstructor
public class ExchangeController {

    private final ExchangeService exchangeService;

    @GetMapping("/exchange")
    @PreAuthorize("hasAuthority('exchange_client')")
    public ResponseEntity<List<ExchangeDto>> getExchange() {
        return ResponseEntity.ok(exchangeService.getExchangeDtoList());
    }

    @PostMapping("/convert")
    @PreAuthorize("hasAuthority('exchange_client')")
    public ResponseEntity<ConvertResponseDto> convert(@RequestBody ConvertRequestDto convertRequestDto) {
        return ResponseEntity.ok(exchangeService.convert(convertRequestDto));
    }
}