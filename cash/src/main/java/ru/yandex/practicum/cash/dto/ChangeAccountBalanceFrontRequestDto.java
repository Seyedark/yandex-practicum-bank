package ru.yandex.practicum.cash.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.cash.enums.ActionEnum;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeAccountBalanceFrontRequestDto {
    String login;
    BigDecimal changeAmount;
    ActionEnum actionEnum;
}