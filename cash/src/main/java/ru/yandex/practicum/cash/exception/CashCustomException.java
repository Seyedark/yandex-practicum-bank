package ru.yandex.practicum.cash.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CashCustomException extends RuntimeException {
    private final List<String> additionalField;
}
