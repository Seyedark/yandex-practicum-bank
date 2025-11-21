package ru.yandex.practicum.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public void saveNotificationEmailRequestDtoList(List<NotificationEmailRequestDto> notificationEmailRequestDtoList) {
        List<NotificationEntity> notificationEntityList =
                notificationEmailRequestDtoList.stream()
                        .map(this::mapNotificationEmailRequestDtoToNotificationEntity)
                        .toList();
        notificationRepository.saveAll(notificationEntityList);
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
    @Scheduled(fixedRate = 60000)
    public void processTop100Notifications() {
        List<NotificationEntity> notifications = notificationRepository.findFirst100ByEmailSentFalseOrderByCreatedAtAsc();
        notifications.forEach(x -> {
            x.setEmailSent(true);
            log.info("Сообщение {} успешно направлено на {}", x.getMessage(), x.getEmail());
        });
        notificationRepository.saveAll(notifications);
    }
}