package ru.yandex.practicum.generator.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@WebMvcTest
@AutoConfigureStubRunner(
        ids = "ru.yandex.practicum:exchange:+:stubs:8080",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
public class ExchangeContractTest {

    @Test
    void exchangeTest() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        String requestBody = """
                [
                    {
                        "currency": "USD",
                        "purchaseRate": 2,
                        "sellingRate": 1
                    }
                ]
                """;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:8080/exchange/exchange",
                HttpMethod.POST,
                request,
                Void.class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}