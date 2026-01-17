package ru.yandex.practicum.notification.kafka;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.notification.SpringBootPostgreSQLBase;
import ru.yandex.practicum.notification.TestKafkaConfig;
import ru.yandex.practicum.notification.dao.repository.NotificationRepository;
import ru.yandex.practicum.notification.dto.NotificationEmailRequestDto;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = "spring.kafka.consumer.group-id=notification-${random.uuid}")
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1,
        topics = "account-notification-topic",
        brokerProperties = {"listeners=PLAINTEXT://localhost:9093",
                "port=9093"})
@DisplayName("Класс для проверки взаимодействия с кафкой")
@Import(TestKafkaConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class KafkaTest extends SpringBootPostgreSQLBase {

    @Autowired
    private KafkaTemplate<String, NotificationEmailRequestDto> kafkaTemplate;

    @SpyBean
    NotificationRepository notificationRepository;

    @AfterEach
    void cleanup() {
        notificationRepository.deleteAll();
    }

    @Test
    @DisplayName("Проверка сохранения уведомлений")
    void saveNotificationEmailRequestDtoListTest() {
        NotificationEmailRequestDto notificationEmailRequestDto = new NotificationEmailRequestDto();
        String message = "Тестовое сообщение";
        String email = "email@mail.ru";

        notificationEmailRequestDto.setMessage(message);
        notificationEmailRequestDto.setEmail(email);

        await().atMost(10, TimeUnit.SECONDS)
                .until(() -> kafkaTemplate != null);

        kafkaTemplate.send("account-notification-topic", notificationEmailRequestDto);

        verify(notificationRepository, timeout(30000).atLeastOnce())
                .save(any());
    }
}