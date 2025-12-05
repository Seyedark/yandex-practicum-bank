package ru.yandex.practicum.cash.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CashErrorEnum {
    BLOCK_ERROR("Операция временно заблокирована"),
    BALANCE_ERROR("Сумма списания %.2f превышает сумму текущего баланса %.2f");
    private final String message;
}
