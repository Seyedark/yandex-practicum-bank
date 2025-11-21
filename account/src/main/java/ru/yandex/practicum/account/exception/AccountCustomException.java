package ru.yandex.practicum.account.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class AccountCustomException extends RuntimeException {
    private final List<String> additionalField;
}