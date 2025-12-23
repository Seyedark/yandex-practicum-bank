package ru.yandex.practicum.exchange.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConvertRequestDto {
    BigDecimal convertAmount;
    String currencyFrom;
    String currencyTo;
}