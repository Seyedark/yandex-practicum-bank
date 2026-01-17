package ru.yandex.practicum.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.notification.dao.entity.NotificationEntity;
import ru.yandex.practicum.notification.dao.repository.NotificationRepository;
import ru.yandex.practicum.notification.dto.NotificationEmailRequestDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationListenerService {

    private final NotificationRepository notificationRepository;

    @KafkaListener(topics = {
            "${spring.kafka.topic.account}",
            "${spring.kafka.topic.cash}",
            "${spring.kafka.topic.transfer}"
    },
            groupId = "${spring.kafka.consumer.group-id}")
    public void listen(NotificationEmailRequestDto dto,
                       Acknowledgment ack) {
        notificationRepository.save(mapNotificationEmailRequestDtoToNotificationEntity(dto));
        ack.acknowledge();
    }

    private NotificationEntity mapNotificationEmailRequestDtoToNotificationEntity(NotificationEmailRequestDto notificationEmailRequestDto) {
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setMessage(notificationEmailRequestDto.getMessage());
        notificationEntity.setEmailSent(false);
        notificationEntity.setEmail(notificationEmailRequestDto.getEmail());
        return notificationEntity;
    }
}