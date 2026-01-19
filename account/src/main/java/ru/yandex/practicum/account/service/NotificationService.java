package ru.yandex.practicum.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.account.dao.entity.NotificationEntity;
import ru.yandex.practicum.account.dao.repository.NotificationRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationKafkaService notificationKafkaService;

    @Scheduled(initialDelay = 60000, fixedRate = 60000)
    public void processTop100Notifications() {
        List<NotificationEntity> notificationEntityList = notificationRepository.findFirst100ByNotificationSentFalseOrderByCreatedAtAsc();
        if (notificationEntityList != null && !notificationEntityList.isEmpty()) {
            for (NotificationEntity notificationEntity : notificationEntityList) {
                try {
                    notificationKafkaService.sendToKafka(notificationEntity);
                    notificationEntity.setNotificationSent(true);
                    notificationRepository.save(notificationEntity);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
    }
}