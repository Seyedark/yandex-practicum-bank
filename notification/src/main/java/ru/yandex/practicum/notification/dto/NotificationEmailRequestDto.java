package ru.yandex.practicum.notification.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationEmailRequestDto {
    String email;
    String message;
}