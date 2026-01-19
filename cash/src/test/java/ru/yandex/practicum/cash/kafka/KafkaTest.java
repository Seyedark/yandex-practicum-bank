package ru.yandex.practicum.cash.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.cash.SpringBootPostgreSQLBase;
import ru.yandex.practicum.cash.TestKafkaConfig;
import ru.yandex.practicum.cash.TestSecurityConfig;
import ru.yandex.practicum.cash.dao.entity.NotificationEntity;
import ru.yandex.practicum.cash.dao.repository.NotificationRepository;
import ru.yandex.practicum.cash.dto.NotificationEmailRequestDto;
import ru.yandex.practicum.cash.service.NotificationService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1,
        topics = {"cash-notification-topic"},
        brokerProperties = {"listeners=PLAINTEXT://localhost:9093",
                "port=9093"})
@Import({TestKafkaConfig.class, TestSecurityConfig.class})
public class KafkaTest extends SpringBootPostgreSQLBase {

    @SpyBean
    NotificationRepository notificationRepository;

    @Autowired
    NotificationService notificationService;

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    private OAuth2AuthorizedClientService auth2AuthorizedClientService;

    @Autowired
    private ConsumerFactory<String, NotificationEmailRequestDto> consumerFactory;

    @Test
    @DisplayName("Проверка отправки сообщения с уведомлениями")
    void shouldSendNotificationSuccessfully() {
        NotificationEntity notificationEntity = new NotificationEntity();

        String message = "Тестовое сообщение";
        String email = "email@mail.ru";

        notificationEntity.setMessage(message);
        notificationEntity.setEmail(email);
        notificationEntity.setNotificationSent(false);

        notificationRepository.save(notificationEntity);
        notificationService.processTop100Notifications();

        try (Consumer<String, NotificationEmailRequestDto> consumer =
                     consumerFactory.createConsumer("test-group", "test-client")) {

            consumer.subscribe(List.of("cash-notification-topic"));

            ConsumerRecords<String, NotificationEmailRequestDto> records =
                    KafkaTestUtils.getRecords(consumer);

            assertThat(records.count()).isGreaterThan(0);

            ConsumerRecord<String, NotificationEmailRequestDto> received =
                    records.iterator().next();
            assertThat(received.value()).isNotNull();
            assertEquals(message, received.value().getMessage());
            assertEquals(email, received.value().getEmail());
        }
    }
}