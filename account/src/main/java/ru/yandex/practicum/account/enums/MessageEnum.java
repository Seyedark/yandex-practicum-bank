package ru.yandex.practicum.account.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageEnum {
    CREATE_ACCOUNT("Поздравляем с созданием аккаунта в нашем приложении"),
    PASSWORD_CHANGE("Ваш пароль был успешно изменён");
    private final String message;
}