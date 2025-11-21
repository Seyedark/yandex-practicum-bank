package ru.yandex.practicum.front.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KeycloakEnum {
    ACCOUNT("account-client", "system"),
    CASH("cash-client", "system"),
    TRANSFER("transfer-client", "system");
    private final String clientId;
    private final String principal;
}