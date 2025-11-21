package ru.yandex.practicum.transfer.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransferErrorEnum {
    BALANCE_ERROR("Сумма перевода %.2f превышает сумму текущего баланса %.2f");
    private final String message;
}
