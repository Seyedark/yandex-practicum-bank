package ru.yandex.practicum.front.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.front.dto.AccountWithUsersDto;
import ru.yandex.practicum.front.dto.ExchangeDto;
import ru.yandex.practicum.front.dto.TransferFrontRequestDto;
import ru.yandex.practicum.front.enums.KeycloakEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {

    @Value("${urls.transfer.patch}")
    private String transferUrl;
    @Value("${urls.transfer.get}")
    private String transferExchangeListUrl;

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
        restTemplate.patchForObject(transferUrl, request, Void.class);
        return new ArrayList<>();
    }

    @Retry(name = "transferService")
    @CircuitBreaker(name = "transferService", fallbackMethod = "fallbackGetExchangeDtoList")
    public List<ExchangeDto> getExchangeDtoList() {
        HttpEntity<Void> request = new HttpEntity<>(oAuth2Service.formHeadersWithToken(KeycloakEnum.TRANSFER));
        ResponseEntity<List<ExchangeDto>> response = restTemplate.exchange(transferExchangeListUrl, HttpMethod.GET,
                request, new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    public List<String> fallbackTransfer(String login, String loginTo, BigDecimal transferAmount, String currencyTo, String currencyFrom, Exception exception) {
        return fallbackProcessService.basicUnprocessableEntityFallback(exception);
    }

    public List<ExchangeDto> fallbackGetExchangeDtoList(Exception exception) {
        return fallbackProcessService.fallbackGetExchangeDtoList(exception);
    }
}