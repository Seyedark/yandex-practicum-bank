package ru.yandex.practicum.account.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountInfoChangeDto {
    String login;
    String firstName;
    String lastName;
    LocalDate birthDate;
}