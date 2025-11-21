package ru.yandex.practicum.account.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountPasswordChangeDto {
    String login;
    String password;
}