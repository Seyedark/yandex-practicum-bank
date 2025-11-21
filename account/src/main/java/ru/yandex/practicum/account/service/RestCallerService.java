package ru.yandex.practicum.account.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.account.dto.NotificationEmailRequestDto;
import ru.yandex.practicum.account.enums.KeycloakEnum;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestCallerService {

    @Value("${urls.notification}")
    private String notificationUrl;

    private final RestTemplate restTemplate;
    private final OAuth2AuthorizedClientManager clientManager;

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

    private void fallbackSendNotifications(List<NotificationEmailRequestDto> notificationEmailRequestDtoList, Exception exception) {
        defaultFallbackLogic(exception);
    }

    private void defaultFallbackLogic(Exception exception) {
        log.error(exception.getMessage());
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}