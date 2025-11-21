package ru.yandex.practicum.transfer.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransferRequestDto {
    String loginFrom;
    BigDecimal balanceFrom;
    String loginTo;
    BigDecimal balanceTo;
}