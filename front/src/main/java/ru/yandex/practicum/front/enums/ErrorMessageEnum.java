package ru.yandex.practicum.front.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessageEnum {
    USER_NOT_FOUND("Пользователь c логином %s не найден.\n Можно зарегистрироваться по адресу %s"),
    SERVICE_ERROR("Ошибка в работе сервиса. Просьба повторить запрос позднее");
    private final String message;
}