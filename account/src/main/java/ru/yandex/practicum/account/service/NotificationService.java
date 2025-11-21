package ru.yandex.practicum.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.account.dao.entity.NotificationEntity;
import ru.yandex.practicum.account.dao.repository.NotificationRepository;
import ru.yandex.practicum.account.dto.NotificationEmailRequestDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final RestCallerService restCallerService;
    private final NotificationRepository notificationRepository;

    @Scheduled(initialDelay = 60000, fixedRate = 60000)
    public void processTop100Notifications() {
        List<NotificationEntity> notificationEntityList = notificationRepository.findFirst100ByNotificationSentFalseOrderByCreatedAtAsc();
        if (notificationEntityList != null && !notificationEntityList.isEmpty()) {
            List<NotificationEmailRequestDto> notificationEmailRequestDtoList = notificationEntityList
                    .stream()
                    .map(this::mapNotificationEntityToNotificationEmailRequestDto)
                    .toList();
            try {
                restCallerService.sendNotifications(notificationEmailRequestDtoList);
                notificationEntityList.forEach(x -> x.setNotificationSent(true));
                notificationRepository.saveAll(notificationEntityList);
            } catch (Exception e) {
                log.error(e.getMessage());
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