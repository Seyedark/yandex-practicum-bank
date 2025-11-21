package ru.yandex.practicum.transfer.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.transfer.TestSecurityConfig;
import ru.yandex.practicum.transfer.controller.TransferController;

import static org.junit.Assert.assertEquals;

@WebMvcTest
@AutoConfigureStubRunner(
        ids = "ru.yandex.practicum:notification:+:stubs:8080",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@Import(TestSecurityConfig.class)
public class NotificationServiceContractTest {

    @MockBean
    private TransferService transferService;

    @MockBean
    private TransferController transferController;


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