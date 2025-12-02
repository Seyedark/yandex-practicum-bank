package ru.yandex.practicum.front.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.front.dto.TransferFrontRequestDto;
import ru.yandex.practicum.front.enums.KeycloakEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {

    @Value("${urls.transfer}")
    private String changeAccountBalanceUrl;

    private final RestTemplate restTemplate;
    private final FallbackProcessService fallbackProcessService;
    private final OAuth2Service oAuth2Service;


    @Retry(name = "transferService")
    @CircuitBreaker(name = "transferService", fallbackMethod = "fallbackTransfer")
    public List<String> transfer(String login, String loginTo, BigDecimal transferAmount, String currencyTo, String currencyFrom) {
        TransferFrontRequestDto transferFrontRequestDto = new TransferFrontRequestDto();
        transferFrontRequestDto.setLoginFrom(login);
        transferFrontRequestDto.setLoginTo(loginTo);
        transferFrontRequestDto.setTransferAmount(transferAmount);
        transferFrontRequestDto.setCurrencyFrom(currencyFrom);
        transferFrontRequestDto.setCurrencyTo(currencyTo);
        HttpEntity<TransferFrontRequestDto> request =
                new HttpEntity<>(transferFrontRequestDto, oAuth2Service.formHeadersWithToken(KeycloakEnum.TRANSFER));
        restTemplate.patchForObject(changeAccountBalanceUrl, request, Void.class);
        return new ArrayList<>();
    }

    public List<String> fallbackTransfer(String login, String loginTo, BigDecimal transferAmount, String currencyTo, String currencyFrom, Exception exception) {
        return fallbackProcessService.basicUnprocessableEntityFallback(exception);
    }
}