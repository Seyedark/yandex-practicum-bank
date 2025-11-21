package ru.yandex.practicum.cash.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeAccountBalanceRequestDto {
    String login;
    BigDecimal balance;
}