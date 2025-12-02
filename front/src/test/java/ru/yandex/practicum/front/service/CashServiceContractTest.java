package ru.yandex.practicum.front.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.front.controller.FrontController;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest
@AutoConfigureStubRunner(
        ids = "ru.yandex.practicum:cash:+:stubs:8080",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
public class CashServiceContractTest {
    @MockBean
    private FrontController frontController;

    @MockBean
    private AccountService accountService;

    @Test
    void changeAccountBalanceTest() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        String requestBody = """
                {
                login       : "login",
                currency    : "RUB",
                changeAmount: 1,
                actionEnum  : "ACCRUAL"
                }
                """;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:8080/cash",
                HttpMethod.PATCH,
                request,
                Void.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
