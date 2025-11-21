package ru.yandex.practicum.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.notification.dto.NotificationEmailRequestDto;
import ru.yandex.practicum.notification.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/email")
    @PreAuthorize("hasAuthority('notification_client')")
    public ResponseEntity<Void> emailNotification(@RequestBody List<NotificationEmailRequestDto> notificationEmailRequestDtoList) {
        notificationService.saveNotificationEmailRequestDtoList(notificationEmailRequestDtoList);
        return ResponseEntity.ok().build();
    }
}