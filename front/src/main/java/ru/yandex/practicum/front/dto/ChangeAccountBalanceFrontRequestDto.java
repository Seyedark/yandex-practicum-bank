package ru.yandex.practicum.front.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.front.enums.ActionEnum;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeAccountBalanceFrontRequestDto {
    String login;
    String currency;
    BigDecimal changeAmount;
    ActionEnum actionEnum;
}