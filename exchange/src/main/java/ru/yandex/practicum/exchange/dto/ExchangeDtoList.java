package ru.yandex.practicum.exchange.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExchangeDtoList {
    List<ExchangeDto> exchangeDtoList;
}
