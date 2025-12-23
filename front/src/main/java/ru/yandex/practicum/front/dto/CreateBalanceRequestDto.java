package ru.yandex.practicum.front.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateBalanceRequestDto {
    String login;
    String currency;
}
