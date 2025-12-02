package ru.yandex.practicum.transfer.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.transfer.dto.NotificationEmailRequestDto;
import ru.yandex.practicum.transfer.dto.TransferAccountsDto;
import ru.yandex.practicum.transfer.dto.TransferRequestDto;
import ru.yandex.practicum.transfer.enums.KeycloakEnum;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestCallerService {
    @Value("${urls.account.get.transfer}")
    private String getTransferAccountsUrl;

    @Value("${urls.account.patch.transfer}")
    private String patchTransferAccountsUrl;

    @Value("${urls.notification}")
    private String notificationUrl;

    private final RestTemplate restTemplate;
    private final OAuth2AuthorizedClientManager clientManager;


    @Retry(name = "transferService")
    @CircuitBreaker(name = "transferService", fallbackMethod = "fallbackGetTransferAccountsDto")
    public TransferAccountsDto getTransferAccountsDto(String loginFrom, String loginTo, String currencyTo, String currencyFrom) {
        HttpEntity<Void> request = new HttpEntity<>(formHeadersWithToken(KeycloakEnum.ACCOUNT));
        ResponseEntity<TransferAccountsDto> response = restTemplate.exchange(getTransferAccountsUrl, HttpMethod.GET,
                request, TransferAccountsDto.class, loginFrom, loginTo, currencyTo, currencyFrom);
        return response.getBody();
    }

    @Retry(name = "transferService")
    @CircuitBreaker(name = "transferService", fallbackMethod = "fallbackTransfer")
    public void transfer(TransferRequestDto transferRequestDto) {
        HttpEntity<TransferRequestDto> request =
                new HttpEntity<>(transferRequestDto, formHeadersWithToken(KeycloakEnum.ACCOUNT));
        restTemplate.patchForObject(patchTransferAccountsUrl, request, Void.class);
    }

    @Retry(name = "notificationService")
    @CircuitBreaker(name = "notificationService", fallbackMethod = "fallbackSendNotifications")
    public void sendNotifications(List<NotificationEmailRequestDto> notificationEmailRequestDtoList) {
        HttpEntity<List<NotificationEmailRequestDto>> request =
                new HttpEntity<>(notificationEmailRequestDtoList, formHeadersWithToken(KeycloakEnum.NOTIFICATION));
        restTemplate.postForObject(notificationUrl, request, Void.class);
    }

    private HttpHeaders formHeadersWithToken(KeycloakEnum keycloakEnum) {
        OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest
                .withClientRegistrationId(keycloakEnum.getClientId())
                .principal(keycloakEnum.getPrincipal())
                .build();

        OAuth2AuthorizedClient client = clientManager.authorize(request);
        String token = client.getAccessToken().getTokenValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private TransferAccountsDto fallbackGetTransferAccountsDto(String loginFrom, String loginTo, String currencyTo, String currencyFrom, Exception exception) {
        defaultFallbackLogic(exception);
        return null;
    }

    private void fallbackTransfer(TransferRequestDto transferRequestDto, Exception exception) {
        defaultFallbackLogic(exception);
    }

    private void fallbackSendNotifications(List<NotificationEmailRequestDto> notificationEmailRequestDtoList, Exception exception) {
        defaultFallbackLogic(exception);
    }

    private void defaultFallbackLogic(Exception exception) {
        log.error(exception.getMessage());
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}