package ru.yandex.practicum.cash.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountDto {
    String login;
    String password;
    String firstName;
    String lastName;
    String email;
    LocalDate birthDate;
    BigDecimal balance;
}