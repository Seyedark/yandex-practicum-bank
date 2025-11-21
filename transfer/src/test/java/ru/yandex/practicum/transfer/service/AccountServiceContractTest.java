package ru.yandex.practicum.transfer.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.transfer.TestSecurityConfig;
import ru.yandex.practicum.transfer.controller.TransferController;

import static org.junit.Assert.assertEquals;

@WebMvcTest
@AutoConfigureStubRunner(
        ids = "ru.yandex.practicum:account:+:stubs:8080",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@Import(TestSecurityConfig.class)
public class AccountServiceContractTest {

    @MockBean
    private TransferService transferService;

    @MockBean
    private TransferController transferController;


    @Test
    void transferGetTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = new RestTemplate().exchange(
                "http://localhost:8080/account/transfer?loginFrom=login1&loginTo=login2",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void transferPatchTest() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        String requestBody = """
                {
                    "loginFrom": "login1",
                    "balanceFrom": 1,
                    "loginTo": "login2",
                    "balanceTo": 1
                }
                """;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:8080/account/transfer",
                HttpMethod.PATCH,
                request,
                Void.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}