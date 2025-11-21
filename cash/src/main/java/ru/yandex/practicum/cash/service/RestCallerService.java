package ru.yandex.practicum.cash.service;

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
import ru.yandex.practicum.cash.dto.AccountDto;
import ru.yandex.practicum.cash.dto.ChangeAccountBalanceRequestDto;
import ru.yandex.practicum.cash.dto.NotificationEmailRequestDto;
import ru.yandex.practicum.cash.enums.KeycloakEnum;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestCallerService {
    @Value("${urls.account.get.account}")
    private String getAccountUrl;

    @Value("${urls.account.patch.balance}")
    private String changeAccountBalanceUrl;

    @Value("${urls.notification}")
    private String notificationUrl;

    private final RestTemplate restTemplate;
    private final OAuth2AuthorizedClientManager clientManager;


    @Retry(name = "cashService")
    @CircuitBreaker(name = "cashService", fallbackMethod = "fallbackGetAccount")
    public AccountDto getAccount(String login) {
        HttpEntity<Void> request = new HttpEntity<>(formHeadersWithToken(KeycloakEnum.ACCOUNT));
        ResponseEntity<AccountDto> response = restTemplate.exchange(getAccountUrl, HttpMethod.GET, request,
                AccountDto.class, login);
        return response.getBody();
    }

    @Retry(name = "cashService")
    @CircuitBreaker(name = "cashService", fallbackMethod = "fallbackChangeBalance")
    public void changeBalance(ChangeAccountBalanceRequestDto changeAccountBalanceRequestDto) {
        HttpEntity<ChangeAccountBalanceRequestDto> request =
                new HttpEntity<>(changeAccountBalanceRequestDto, formHeadersWithToken(KeycloakEnum.ACCOUNT));
        restTemplate.patchForObject(changeAccountBalanceUrl, request, Void.class);
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

    private AccountDto fallbackGetAccount(String login, Exception exception) {
        defaultFallbackLogic(exception);
        return null;
    }

    private void fallbackChangeBalance(ChangeAccountBalanceRequestDto changeAccountBalanceRequestDto, Exception exception) {
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