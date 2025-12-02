package ru.yandex.practicum.cash.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.cash.TestSecurityConfig;
import ru.yandex.practicum.cash.controller.CashController;

import static org.junit.Assert.assertEquals;

@WebMvcTest
@AutoConfigureStubRunner(
        ids = "ru.yandex.practicum:account:+:stubs:8080",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@Import(TestSecurityConfig.class)
public class AccountServiceContractTest {

    @MockBean
    private CashService cashService;

    @MockBean
    private CashController cashController;

    @Test
    void findBalanceTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = new RestTemplate().exchange(
                "http://localhost:8080/account/balance?login=login&currency=RUB",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void changeAccountBalanceTest() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        String requestBody = """
                {
                    "login": "login",
                    "currency": "RUB",
                    "balance": 1
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:8080/account/balance",
                HttpMethod.PATCH,
                request,
                Void.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}