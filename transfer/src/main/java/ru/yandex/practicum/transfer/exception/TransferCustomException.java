package ru.yandex.practicum.transfer.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class TransferCustomException extends RuntimeException {
    private final List<String> additionalField;
}
