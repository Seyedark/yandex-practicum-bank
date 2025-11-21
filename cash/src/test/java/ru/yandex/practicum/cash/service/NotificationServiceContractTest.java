package ru.yandex.practicum.cash.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.cash.SpringBootPostgreSQLBase;
import ru.yandex.practicum.cash.TestSecurityConfig;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@AutoConfigureStubRunner(
        ids = "ru.yandex.practicum:notification:+:stubs:8080",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@Import(TestSecurityConfig.class)
public class NotificationServiceContractTest extends SpringBootPostgreSQLBase {

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    private OAuth2AuthorizedClientService authorizedClientService;

    @MockBean
    private RestCallerService restCallerService;

    @Test
    void sendEmailNotificationsToStubTest() {
        String requestBody = """
                [
                    {
                        "email": "email@mail.ru",
                        "message": "Тестовое сообщение"
                    },
                    {
                        "email": "email@mail.ru",
                        "message": "Тестовое сообщение"
                    }
                ]
                """;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Void> response = new RestTemplate().postForEntity(
                "http://localhost:8080/notification/email",
                request,
                Void.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}