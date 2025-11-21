package ru.yandex.practicum.transfer.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageEnum {
    ACCRUAL("На ваш счёт успешно зачислены денежные средства в размере %.2f"),
    WRITE_OFF("С вашего счёта успешно списаны денежные средства в размере %.2f");
    private final String message;
}