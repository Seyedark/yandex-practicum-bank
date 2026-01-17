package ru.yandex.practicum.notification.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.notification.SpringBootPostgreSQLBase;
import ru.yandex.practicum.notification.TestKafkaConfig;
import ru.yandex.practicum.notification.dao.entity.NotificationEntity;
import ru.yandex.practicum.notification.dao.repository.NotificationRepository;
import ru.yandex.practicum.notification.dto.NotificationEmailRequestDto;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = "spring.kafka.consumer.group-id=notification-${random.uuid}")
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1,
        topics = "account-notification-topic",
        brokerProperties = {"listeners=PLAINTEXT://localhost:9093",
                "port=9093"})
@DisplayName("Класс для проверки взаимодействия с сервисом уведомлений и с базой")
@Import(TestKafkaConfig.class)
public class NotificationServiceTest extends SpringBootPostgreSQLBase {

    @SpyBean
    NotificationRepository notificationRepository;

    @Autowired
    NotificationService notificationService;

    @Autowired
    private KafkaTemplate<String, NotificationEmailRequestDto> kafkaTemplate;

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


    @Test
    @DisplayName("Проверка отправки уведомлений")
    void processTop100NotificationsTest() {
        NotificationEntity notificationEntity = new NotificationEntity();

        String message = "Тестовое сообщение";
        String email = "email@mail.ru";

        notificationEntity.setMessage(message);
        notificationEntity.setEmail(email);
        notificationEntity.setEmailSent(false);

        notificationRepository.save(notificationEntity);
        notificationService.processTop100Notifications();

        List<NotificationEntity> notificationEntityList = notificationRepository.findAll();

        assertEquals(1, notificationEntityList.size());
        assertEquals(message, notificationEntityList.get(0).getMessage());
        assertEquals(email, notificationEntityList.get(0).getEmail());
        assertEquals(true, notificationEntityList.get(0).getEmailSent());
    }
}