package ru.yandex.practicum.account.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShortAccountDto {
    String login;
    String firstName;
    String lastName;
    List<AccountBalanceDto> accountBalanceDtoList;
}