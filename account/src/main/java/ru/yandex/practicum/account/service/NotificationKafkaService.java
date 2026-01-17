package ru.yandex.practicum.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.account.dao.entity.NotificationEntity;
import ru.yandex.practicum.account.dto.NotificationEmailRequestDto;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationKafkaService {
    private final KafkaTemplate<String, NotificationEmailRequestDto> kafkaTemplate;

    @Value("${spring.kafka.topic.notification}")
    private String notificationTopic;

    public void sendToKafka(NotificationEntity notificationEntity) {
        UUID uuid = UUID.randomUUID();
        kafkaTemplate.send(notificationTopic, uuid.toString(),
                mapNotificationEntityToNotificationEmailRequestDto(notificationEntity));
    }

    private NotificationEmailRequestDto mapNotificationEntityToNotificationEmailRequestDto(NotificationEntity notificationEntity) {
        NotificationEmailRequestDto notificationEmailRequestDto = new NotificationEmailRequestDto();
        notificationEmailRequestDto.setEmail(notificationEntity.getEmail());
        notificationEmailRequestDto.setMessage(notificationEntity.getMessage());
        return notificationEmailRequestDto;
    }
}