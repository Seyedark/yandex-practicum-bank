package ru.yandex.practicum.generator.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KeycloakEnum {
    EXCHANGE("exchange-client", "system");
    private final String clientId;
    private final String principal;
}