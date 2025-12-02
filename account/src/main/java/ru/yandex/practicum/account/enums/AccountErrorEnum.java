package ru.yandex.practicum.account.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountErrorEnum {
    LOGIN("Логин пользователя должен быть больше 3 символов"),
    PASSWORD("Пароль должен быть больше 3 символов"),
    FIRST_NAME("Имя пользователя должно быть больше 3 символов"),
    LAST_NAME("Фамилия пользователя должна быть больше 3 символов"),
    LOGIN_ALREADY_EXIST("Пользователь с таким логином уже существует"),
    EMAIL_FORMAT("Email должен быть в корректном формате"),
    AGE("Возраст пользователя должен быть больше 18 лет"),
    CURRENCY_ALREADY_EXIST("У вас уже существует счёт с данной валютой");
    private final String message;
}