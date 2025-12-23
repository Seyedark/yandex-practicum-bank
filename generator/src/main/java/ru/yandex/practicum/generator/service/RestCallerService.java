package ru.yandex.practicum.generator.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.generator.dto.ExchangeDto;
import ru.yandex.practicum.generator.enums.KeycloakEnum;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestCallerService {

    @Value("${urls.exchange}")
    private String exchangeUrl;

    private final RestTemplate restTemplate;
    private final OAuth2AuthorizedClientManager clientManager;

    @Retry(name = "exchangeService")
    @CircuitBreaker(name = "exchangeService", fallbackMethod = "fallbackGetExchangeDtoList")
    public void sendNewExchangeDtoList(List<ExchangeDto> exchangeDtoList) {
        HttpEntity<List<ExchangeDto>> request =
                new HttpEntity<>(exchangeDtoList, formHeadersWithToken(KeycloakEnum.EXCHANGE));
        restTemplate.postForObject(exchangeUrl, request, Void.class);
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

    private void fallbackGetExchangeDtoList(List<ExchangeDto> exchangeDtoList, Exception exception) {
        log.error(exception.getMessage());
    }
}