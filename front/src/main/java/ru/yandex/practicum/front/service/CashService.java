package ru.yandex.practicum.front.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.front.dto.ChangeAccountBalanceFrontRequestDto;
import ru.yandex.practicum.front.enums.ActionEnum;
import ru.yandex.practicum.front.enums.KeycloakEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CashService {

    @Value("${urls.cash}")
    private String changeAccountBalanceUrl;

    private final RestTemplate restTemplate;
    private final FallbackProcessService fallbackProcessService;
    private final OAuth2Service oAuth2Service;

    @Retry(name = "cashService")
    @CircuitBreaker(name = "cashService", fallbackMethod = "fallbackChangeAccountBalance")
    public List<String> changeAccountBalance(String login, ActionEnum actionEnum, BigDecimal balance, String currency) {
        ChangeAccountBalanceFrontRequestDto changeAccountBalanceFrontRequestDto = new ChangeAccountBalanceFrontRequestDto();
        changeAccountBalanceFrontRequestDto.setLogin(login);
        changeAccountBalanceFrontRequestDto.setActionEnum(actionEnum);
        changeAccountBalanceFrontRequestDto.setCurrency(currency);
        changeAccountBalanceFrontRequestDto.setChangeAmount(balance);
        HttpEntity<ChangeAccountBalanceFrontRequestDto> request =
                new HttpEntity<>(changeAccountBalanceFrontRequestDto, oAuth2Service.formHeadersWithToken(KeycloakEnum.CASH));
        restTemplate.patchForObject(changeAccountBalanceUrl, request, Void.class);
        return new ArrayList<>();
    }

    public List<String> fallbackChangeAccountBalance(String login, ActionEnum actionEnum, BigDecimal balance, String currency, Exception exception) {
        return fallbackProcessService.basicUnprocessableEntityFallback(exception);
    }
}