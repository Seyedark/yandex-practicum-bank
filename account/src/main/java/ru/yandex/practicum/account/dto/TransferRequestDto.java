package ru.yandex.practicum.account.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransferRequestDto {
    String loginFrom;
    String currencyFrom;
    BigDecimal balanceFrom;
    String loginTo;
    String currencyTo;
    BigDecimal balanceTo;
}