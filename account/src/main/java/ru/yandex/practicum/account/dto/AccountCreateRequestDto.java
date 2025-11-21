package ru.yandex.practicum.account.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountCreateRequestDto {
    String login;
    String firstName;
    String lastName;
    String email;
    LocalDate birthDate;
    String password;
}