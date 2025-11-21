package ru.yandex.practicum.notification.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.notification.SpringBootPostgreSQLBase;
import ru.yandex.practicum.notification.TestSecurityConfig;
import ru.yandex.practicum.notification.dao.entity.NotificationEntity;
import ru.yandex.practicum.notification.dao.repository.NotificationRepository;
import ru.yandex.practicum.notification.dto.NotificationEmailRequestDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(TestSecurityConfig.class)
@DisplayName("Класс для проверки взаимодействия с сервисом уведомлений и с базой")
public class NotificationServiceTest extends SpringBootPostgreSQLBase {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    NotificationService notificationService;

    @BeforeEach
    void cleanup() {
        notificationRepository.deleteAll();
    }

    @Test
    @DisplayName("Проверка сохранения уведомлений")
    void saveNotificationEmailRequestDtoListTest() {
        List<NotificationEmailRequestDto> notificationEmailRequestDtoList = new ArrayList<>();
        NotificationEmailRequestDto notificationEmailRequestDto = new NotificationEmailRequestDto();

        String message = "Тестовое сообщение";
        String email = "email@mail.ru";

        notificationEmailRequestDto.setMessage(message);
        notificationEmailRequestDto.setEmail(email);

        notificationEmailRequestDtoList.add(notificationEmailRequestDto);

        notificationService.saveNotificationEmailRequestDtoList(notificationEmailRequestDtoList);

        List<NotificationEntity> notificationEntityList = notificationRepository.findAll();

        assertEquals(1, notificationEntityList.size());
        assertEquals(message, notificationEntityList.get(0).getMessage());
        assertEquals(email, notificationEntityList.get(0).getEmail());
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