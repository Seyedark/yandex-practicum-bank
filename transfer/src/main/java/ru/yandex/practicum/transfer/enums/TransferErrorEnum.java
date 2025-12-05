package ru.yandex.practicum.transfer.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransferErrorEnum {
    BLOCK_ERROR("Операция временно заблокирована"),
    BALANCE_ERROR("Сумма перевода %.2f превышает сумму текущего баланса %.2f");
    private final String message;
}
