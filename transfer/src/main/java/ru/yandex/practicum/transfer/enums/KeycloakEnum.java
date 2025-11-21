package ru.yandex.practicum.transfer.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KeycloakEnum {
    ACCOUNT("account-client", "system"),
    NOTIFICATION("notification-client", "system");
    private final String clientId;
    private final String principal;
}