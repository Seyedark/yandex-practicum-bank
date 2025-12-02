package ru.yandex.practicum.front.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransferFrontRequestDto {
    String loginFrom;
    String loginTo;
    BigDecimal transferAmount;
    String currencyFrom;
    String currencyTo;
}