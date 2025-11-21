package ru.yandex.practicum.account.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountWithUsersDto {
    String login;
    String password;
    String firstName;
    String lastName;
    String email;
    LocalDate birthDate;
    BigDecimal balance;
    List<ShortAccountDto> shortAccountDtoList;
}