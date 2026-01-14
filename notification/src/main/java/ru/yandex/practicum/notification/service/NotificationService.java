package ru.yandex.practicum.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.notification.dao.entity.NotificationEntity;
import ru.yandex.practicum.notification.dao.repository.NotificationRepository;
import ru.yandex.practicum.notification.dto.NotificationEmailRequestDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @KafkaListener(topics = {
            "${spring.kafka.topic.account}",
            "${spring.kafka.topic.cash}",
            "${spring.kafka.topic.transfer}"
    },
            groupId = "${spring.kafka.consumer.group-id}")
    public void listen(NotificationEmailRequestDto dto,
                       Acknowledgment ack) {
        try {
            notificationRepository.save(mapNotificationEmailRequestDtoToNotificationEntity(dto));
            ack.acknowledge();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    private NotificationEntity mapNotificationEmailRequestDtoToNotificationEntity(NotificationEmailRequestDto notificationEmailRequestDto) {
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setMessage(notificationEmailRequestDto.getMessage());
        notificationEntity.setEmailSent(false);
        notificationEntity.setEmail(notificationEmailRequestDto.getEmail());
        return notificationEntity;
    }

    /**
     * Т.к. метод заглушка нет логики обработки ошибок
     */
    @Scheduled(initialDelay = 60000, fixedRate = 60000)
    public void processTop100Notifications() {
        List<NotificationEntity> notifications = notificationRepository.findFirst100ByEmailSentFalseOrderByCreatedAtAsc();
        notifications.forEach(x -> {
            x.setEmailSent(true);
            log.info("Сообщение {} успешно направлено на {}", x.getMessage(), x.getEmail());
        });
        notificationRepository.saveAll(notifications);
    }
}