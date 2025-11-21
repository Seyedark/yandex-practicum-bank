package ru.yandex.practicum.transfer.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationEmailRequestDto {
    String email;
    String message;
}