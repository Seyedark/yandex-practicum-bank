package ru.yandex.practicum.account.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import ru.yandex.practicum.account.SpringBootPostgreSQLBase;
import ru.yandex.practicum.account.TestSecurityConfig;
import ru.yandex.practicum.account.dao.entity.NotificationEntity;
import ru.yandex.practicum.account.dao.repository.NotificationRepository;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Import(TestSecurityConfig.class)
@DisplayName("Класс для проверки взаимодействия с сервисом уведомлений и с базой")
public class NotificationServiceTest extends SpringBootPostgreSQLBase {

    @SpyBean
    NotificationRepository notificationRepository;

    @Autowired
    NotificationService notificationService;

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    private OAuth2AuthorizedClientService authorizedClientService;

    @MockBean
    private RestCallerService restCallerService;

    @BeforeEach
    void cleanup() {
        notificationRepository.deleteAll();
    }


    @Test
    @DisplayName("Проверка отправки уведомлений")
    void processTop100NotificationsTest() {
        NotificationEntity notificationEntity = new NotificationEntity();

        String message = "Тестовое сообщение";
        String email = "email@mail.ru";

        notificationEntity.setMessage(message);
        notificationEntity.setEmail(email);
        notificationEntity.setNotificationSent(false);

        notificationRepository.save(notificationEntity);

        notificationService.processTop100Notifications();

        verify(restCallerService, times(1)).sendNotifications(anyList());
        verify(notificationRepository, times(1)).saveAll(anyList());
    }
}