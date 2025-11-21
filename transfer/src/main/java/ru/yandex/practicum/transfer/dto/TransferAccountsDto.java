package ru.yandex.practicum.transfer.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransferAccountsDto {
    String loginFrom;
    String emailFrom;
    BigDecimal balanceFrom;
    String loginTo;
    String emailTo;
    BigDecimal balanceTo;
}