package ru.yandex.practicum.account.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KeycloakEnum {
    NOTIFICATION("notification-client", "system");
    private final String clientId;
    private final String principal;
}