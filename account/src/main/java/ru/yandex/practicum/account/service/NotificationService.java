package ru.yandex.practicum.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.account.dao.entity.NotificationEntity;
import ru.yandex.practicum.account.dao.repository.NotificationRepository;
import ru.yandex.practicum.account.dto.NotificationEmailRequestDto;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final KafkaTemplate<String, NotificationEmailRequestDto> kafkaTemplate;
    private final NotificationRepository notificationRepository;

    @Value("${spring.kafka.topic.notification}")
    private String notificationTopic;

    @Scheduled(initialDelay = 60000, fixedRate = 60000)
    public void processTop100Notifications() {
        List<NotificationEntity> notificationEntityList = notificationRepository.findFirst100ByNotificationSentFalseOrderByCreatedAtAsc();
        if (notificationEntityList != null && !notificationEntityList.isEmpty()) {
            for (NotificationEntity notificationEntity : notificationEntityList) {
                try {
                    UUID uuid = UUID.randomUUID();
                    kafkaTemplate.send(notificationTopic, uuid.toString(),
                            mapNotificationEntityToNotificationEmailRequestDto(notificationEntity));
                    notificationEntity.setNotificationSent(true);
                    notificationRepository.save(notificationEntity);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    private NotificationEmailRequestDto mapNotificationEntityToNotificationEmailRequestDto(NotificationEntity notificationEntity) {
        NotificationEmailRequestDto notificationEmailRequestDto = new NotificationEmailRequestDto();
        notificationEmailRequestDto.setEmail(notificationEntity.getEmail());
        notificationEmailRequestDto.setMessage(notificationEntity.getMessage());
        return notificationEmailRequestDto;
    }
}