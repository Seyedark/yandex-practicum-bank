package ru.yandex.practicum.account.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.account.SpringBootPostgreSQLBase;
import ru.yandex.practicum.account.TestSecurityConfig;
import ru.yandex.practicum.account.dao.entity.AccountBalanceEntity;
import ru.yandex.practicum.account.dao.entity.AccountEntity;
import ru.yandex.practicum.account.dao.repository.AccountRepository;
import ru.yandex.practicum.account.dto.*;
import ru.yandex.practicum.account.enums.CurrencyEnum;
import ru.yandex.practicum.account.exception.AccountCustomException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Import(TestSecurityConfig.class)
@DisplayName("Класс для проверки взаимодействия с сервисом аккаунтов и с базой")
public class AccountServiceTest extends SpringBootPostgreSQLBase {

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    void cleanup() {
        accountRepository.deleteAll();
    }


    @Test
    @DisplayName("Пользователь найден по логину")
    void findAccountByLoginSuccess() {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setLogin("login1");
        accountEntity.setFirstName("Тест");
        accountEntity.setLastName("Тестов");
        accountEntity.setEmail("test@mail.ru");
        accountEntity.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity.setPassword("12345");

        AccountBalanceEntity accountBalanceEntity = new AccountBalanceEntity();
        accountBalanceEntity.setCurrency(CurrencyEnum.RUB.name());
        accountBalanceEntity.setBalance(BigDecimal.ZERO);

        List<AccountBalanceEntity> accountBalanceEntityList = new ArrayList<>();
        accountBalanceEntityList.add(accountBalanceEntity);

        accountEntity.setAccountBalances(accountBalanceEntityList);
        accountRepository.save(accountEntity);

        AccountDto result = accountService.findAccountByLogin("login1");

        assertNotNull(result);
        assertEquals("login1", result.getLogin());
        assertEquals("Тест", result.getFirstName());
        assertEquals("Тестов", result.getLastName());
        assertEquals("test@mail.ru", result.getEmail());
    }

    @Test
    @DisplayName("Пользователь не найден по логину")
    void findAccountByLoginFail() {
        assertThatThrownBy(() -> accountService.findAccountByLogin("nonexistent"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                });
    }

    @Test
    @DisplayName("Пользователь и связанные пользователи найдены")
    void findAccountByLoginWithUsersSuccess() {
        AccountEntity accountEntity1 = new AccountEntity();
        accountEntity1.setLogin("login2");
        accountEntity1.setFirstName("Тест");
        accountEntity1.setLastName("Тестов");
        accountEntity1.setEmail("test@mail.ru");
        accountEntity1.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity1.setPassword("12345");
        AccountBalanceEntity accountBalanceEntity1 = new AccountBalanceEntity();
        accountBalanceEntity1.setCurrency(CurrencyEnum.RUB.name());
        accountBalanceEntity1.setBalance(BigDecimal.ZERO);

        List<AccountBalanceEntity> accountBalanceEntityList1 = new ArrayList<>();
        accountBalanceEntityList1.add(accountBalanceEntity1);

        accountEntity1.setAccountBalances(accountBalanceEntityList1);
        accountRepository.save(accountEntity1);

        AccountEntity accountEntity2 = new AccountEntity();
        accountEntity2.setLogin("login3");
        accountEntity2.setFirstName("Тест1");
        accountEntity2.setLastName("Тестов");
        accountEntity2.setEmail("test@mail.ru");
        accountEntity2.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity2.setPassword("12345");
        AccountBalanceEntity accountBalanceEntity2 = new AccountBalanceEntity();
        accountBalanceEntity2.setCurrency(CurrencyEnum.RUB.name());
        accountBalanceEntity2.setBalance(BigDecimal.ZERO);
        List<AccountBalanceEntity> accountBalanceEntityList2 = new ArrayList<>();
        accountBalanceEntityList2.add(accountBalanceEntity2);

        accountEntity1.setAccountBalances(accountBalanceEntityList2);
        accountRepository.save(accountEntity2);
        AccountWithUsersDto accountWithUsersDto = accountService.findAccountByLoginWithUsers("login2");


        assertNotNull(accountWithUsersDto);
        assertEquals("login2", accountWithUsersDto.getLogin());
        assertEquals("Тест", accountWithUsersDto.getFirstName());
        assertEquals("Тестов", accountWithUsersDto.getLastName());
        assertEquals("test@mail.ru", accountWithUsersDto.getEmail());
        assertEquals("Тест1", accountWithUsersDto.getShortAccountDtoList().getFirst().getFirstName());
    }

    @Test
    @DisplayName("Пользователь и связанные пользователи не найдены")
    void findAccountByLoginWithUsersFail() {
        assertThatThrownBy(() -> accountService.findAccountByLoginWithUsers("login4"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }

    @Test
    @DisplayName("Создание нового аккаунта")
    void createAccountSuccess() {
        AccountCreateRequestDto accountCreateRequestDto = new AccountCreateRequestDto();
        accountCreateRequestDto.setLogin("login5");
        accountCreateRequestDto.setFirstName("Тест");
        accountCreateRequestDto.setLastName("Тестов");
        accountCreateRequestDto.setEmail("test@mail.ru");
        accountCreateRequestDto.setBirthDate(LocalDate.now().minusYears(20));
        accountCreateRequestDto.setPassword("12345");

        accountService.createAccount(accountCreateRequestDto);
        AccountEntity result = accountRepository.findByLogin("login5");

        assertNotNull(result);
        assertEquals("login5", result.getLogin());
        assertEquals("Тест", result.getFirstName());
        assertEquals("Тестов", result.getLastName());
        assertEquals("test@mail.ru", result.getEmail());
    }

    @Test
    @DisplayName("Ошибка создания нового аккаунта")
    void createAccountFail() {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setLogin("login6");
        accountEntity.setFirstName("Тест");
        accountEntity.setLastName("Тестов");
        accountEntity.setEmail("test@mail.ru");
        accountEntity.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity.setPassword("12345");

        accountRepository.save(accountEntity);

        AccountCreateRequestDto accountCreateRequestDto = new AccountCreateRequestDto();
        accountCreateRequestDto.setLogin("login6");
        accountCreateRequestDto.setFirstName("Тест");
        accountCreateRequestDto.setLastName("Тестов");
        accountCreateRequestDto.setEmail("test@mail.ru");
        accountCreateRequestDto.setBirthDate(LocalDate.now().minusYears(20));
        accountCreateRequestDto.setPassword("12345");

        assertThatThrownBy(() -> accountService.createAccount(accountCreateRequestDto))
                .isInstanceOf(AccountCustomException.class);
    }

    @Test
    @DisplayName("Изменение пароля")
    void changePasswordSuccess() {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setLogin("login7");
        accountEntity.setFirstName("Тест");
        accountEntity.setLastName("Тестов");
        accountEntity.setEmail("test@mail.ru");
        accountEntity.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity.setPassword("12345");

        accountRepository.save(accountEntity);

        AccountPasswordChangeDto accountPasswordChangeDto = new AccountPasswordChangeDto();
        accountPasswordChangeDto.setLogin("login7");
        accountPasswordChangeDto.setPassword("123456");

        accountService.changePassword(accountPasswordChangeDto);

        AccountEntity result = accountRepository.findByLogin("login7");

        assertEquals("123456", result.getPassword());
    }

    @Test
    @DisplayName("Ошибка изменения пароля")
    void changePasswordFail() {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setLogin("login8");
        accountEntity.setFirstName("Тест");
        accountEntity.setLastName("Тестов");
        accountEntity.setEmail("test@mail.ru");
        accountEntity.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity.setPassword("12345");

        accountRepository.save(accountEntity);

        AccountPasswordChangeDto accountPasswordChangeDto = new AccountPasswordChangeDto();
        accountPasswordChangeDto.setLogin("login8");
        accountPasswordChangeDto.setPassword("123");

        assertThatThrownBy(() -> accountService.changePassword(accountPasswordChangeDto))
                .isInstanceOf(AccountCustomException.class);
    }

    @Test
    @DisplayName("Смена информации об аккаунте")
    void changeInfoSuccess() {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setLogin("login9");
        accountEntity.setFirstName("Тест");
        accountEntity.setLastName("Тестов");
        accountEntity.setEmail("test@mail.ru");
        accountEntity.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity.setPassword("12345");

        accountRepository.save(accountEntity);

        AccountInfoChangeDto accountInfoChangeDto = new AccountInfoChangeDto();
        accountInfoChangeDto.setLogin("login9");
        accountInfoChangeDto.setFirstName("Тест1");
        accountInfoChangeDto.setLastName("Тестов1");
        accountInfoChangeDto.setBirthDate(LocalDate.now().minusYears(21));

        accountService.changeInfo(accountInfoChangeDto);

        AccountEntity result = accountRepository.findByLogin("login9");

        assertEquals("Тест1", result.getFirstName());
        assertEquals("Тестов1", result.getLastName());
        assertEquals(LocalDate.now().minusYears(21), result.getBirthDate());

    }


    @Test
    @DisplayName("Ошибка смены информации об аккаунта")
    void changeInfoFail() {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setLogin("login11");
        accountEntity.setFirstName("Тест");
        accountEntity.setLastName("Тестов");
        accountEntity.setEmail("test@mail.ru");
        accountEntity.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity.setPassword("12345");

        accountRepository.save(accountEntity);

        AccountInfoChangeDto accountInfoChangeDto = new AccountInfoChangeDto();
        accountInfoChangeDto.setLogin("login11");
        accountInfoChangeDto.setFirstName("Т");
        accountInfoChangeDto.setLastName("Т");
        accountInfoChangeDto.setBirthDate(LocalDate.now().minusYears(1));

        assertThatThrownBy(() -> accountService.changeInfo(accountInfoChangeDto))
                .isInstanceOf(AccountCustomException.class);
    }

    @Test
    @Transactional
    @DisplayName("Смена баланса")
    void changeBalanceSuccess() {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setLogin("login12");
        accountEntity.setFirstName("Тест");
        accountEntity.setLastName("Тестов");
        accountEntity.setEmail("test@mail.ru");
        accountEntity.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity.setPassword("12345");
        AccountBalanceEntity accountBalanceEntity = new AccountBalanceEntity();
        accountBalanceEntity.setCurrency(CurrencyEnum.RUB.name());
        accountBalanceEntity.setBalance(BigDecimal.ZERO);

        List<AccountBalanceEntity> accountBalanceEntityList = new ArrayList<>();
        accountBalanceEntityList.add(accountBalanceEntity);

        accountEntity.setAccountBalances(accountBalanceEntityList);
        AccountEntity accountAfterSave = accountRepository.save(accountEntity);


        ChangeAccountBalanceRequestDto changeAccountBalanceRequestDto = new ChangeAccountBalanceRequestDto();
        changeAccountBalanceRequestDto.setLogin("login12");
        changeAccountBalanceRequestDto.setBalance(BigDecimal.ONE);
        changeAccountBalanceRequestDto.setCurrency(accountAfterSave.getAccountBalances().get(0).getCurrency());

        accountService.changeBalance(changeAccountBalanceRequestDto);

        AccountEntity result = accountRepository.findByLogin("login12");

        assertThat(result.getAccountBalances().getFirst().getBalance()).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    @DisplayName("Ошибка смены баланса")
    void changeBalanceFail() {
        ChangeAccountBalanceRequestDto changeAccountBalanceRequestDto = new ChangeAccountBalanceRequestDto();
        changeAccountBalanceRequestDto.setLogin("login13");
        changeAccountBalanceRequestDto.setBalance(BigDecimal.ONE);
        changeAccountBalanceRequestDto.setCurrency(CurrencyEnum.RUB.name());
        assertThatThrownBy(() -> accountService.changeBalance(changeAccountBalanceRequestDto))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }

    @Test
    @DisplayName("Получение аккаунтов для перевода")
    void getTransferAccountsDtoSuccess() {
        AccountEntity accountEntity1 = new AccountEntity();
        accountEntity1.setLogin("login14");
        accountEntity1.setFirstName("Тест");
        accountEntity1.setLastName("Тестов");
        accountEntity1.setEmail("test@mail.ru");
        accountEntity1.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity1.setPassword("12345");

        AccountBalanceEntity accountBalanceEntity1 = new AccountBalanceEntity();
        accountBalanceEntity1.setCurrency(CurrencyEnum.RUB.name());
        accountBalanceEntity1.setBalance(BigDecimal.ZERO);

        List<AccountBalanceEntity> accountBalanceEntityList1 = new ArrayList<>();
        accountBalanceEntityList1.add(accountBalanceEntity1);

        accountEntity1.setAccountBalances(accountBalanceEntityList1);
        AccountEntity accountAfterSave1 = accountRepository.save(accountEntity1);

        AccountEntity accountEntity2 = new AccountEntity();
        accountEntity2.setLogin("login15");
        accountEntity2.setFirstName("Тест1");
        accountEntity2.setLastName("Тестов");
        accountEntity2.setEmail("test@mail.ru");
        accountEntity2.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity2.setPassword("12345");

        AccountBalanceEntity accountBalanceEntity2 = new AccountBalanceEntity();
        accountBalanceEntity2.setCurrency(CurrencyEnum.USD.name());
        accountBalanceEntity2.setBalance(BigDecimal.ONE);
        List<AccountBalanceEntity> accountBalanceEntityList2 = new ArrayList<>();
        accountBalanceEntityList2.add(accountBalanceEntity2);

        accountEntity2.setAccountBalances(accountBalanceEntityList2);
        AccountEntity accountAfterSave2 = accountRepository.save(accountEntity2);
        TransferAccountsDto transferAccountsDto = accountService
                .getTransferAccountsDto("login14", accountAfterSave1.getAccountBalances().get(0).getCurrency(),
                        "login15", accountAfterSave2.getAccountBalances().get(0).getCurrency());
        assertNotNull(transferAccountsDto);
        assertThat(transferAccountsDto.getBalanceFrom()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(transferAccountsDto.getBalanceTo()).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    @DisplayName("Ошибка получения аккаунтов для перевода")
    void getTransferAccountsDtoFail() {
        assertThatThrownBy(() -> accountService.getTransferAccountsDto("login16", CurrencyEnum.RUB.name(),
                "login17", CurrencyEnum.RUB.name()))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }

    @Test
    @Transactional
    @DisplayName("Перевод")
    void transferSuccess() {
        AccountEntity accountEntity1 = new AccountEntity();
        accountEntity1.setLogin("login18");
        accountEntity1.setFirstName("Тест");
        accountEntity1.setLastName("Тестов");
        accountEntity1.setEmail("test@mail.ru");
        accountEntity1.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity1.setPassword("12345");

        AccountBalanceEntity accountBalanceEntity1 = new AccountBalanceEntity();
        accountBalanceEntity1.setCurrency(CurrencyEnum.RUB.name());
        accountBalanceEntity1.setBalance(BigDecimal.ZERO);

        List<AccountBalanceEntity> accountBalanceEntityList1 = new ArrayList<>();
        accountBalanceEntityList1.add(accountBalanceEntity1);
        accountEntity1.setAccountBalances(accountBalanceEntityList1);
        AccountEntity accountAfterSave1 = accountRepository.save(accountEntity1);

        AccountEntity accountEntity2 = new AccountEntity();
        accountEntity2.setLogin("login19");
        accountEntity2.setFirstName("Тест1");
        accountEntity2.setLastName("Тестов");
        accountEntity2.setEmail("test@mail.ru");
        accountEntity2.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity2.setPassword("12345");
        AccountBalanceEntity accountBalanceEntity2 = new AccountBalanceEntity();
        accountBalanceEntity2.setCurrency(CurrencyEnum.USD.name());
        accountBalanceEntity2.setBalance(BigDecimal.ONE);
        List<AccountBalanceEntity> accountBalanceEntityList2 = new ArrayList<>();
        accountBalanceEntityList2.add(accountBalanceEntity2);

        accountEntity2.setAccountBalances(accountBalanceEntityList2);
        AccountEntity accountAfterSave2 = accountRepository.save(accountEntity2);

        accountRepository.save(accountEntity2);

        TransferRequestDto transferAccountsDto = new TransferRequestDto();
        transferAccountsDto.setLoginFrom("login18");
        transferAccountsDto.setCurrencyFrom(CurrencyEnum.RUB.name());
        transferAccountsDto.setBalanceFrom(BigDecimal.ZERO);
        transferAccountsDto.setLoginTo("login19");
        transferAccountsDto.setCurrencyTo(CurrencyEnum.USD.name());
        transferAccountsDto.setBalanceTo(BigDecimal.ONE);

        accountService.transfer(transferAccountsDto);

        AccountEntity result1 = accountRepository.findByLogin("login18");
        AccountEntity result2 = accountRepository.findByLogin("login19");

        assertThat(result1.getAccountBalances().getFirst().getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result2.getAccountBalances().getFirst().getBalance()).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    @DisplayName("Перевод завершился с ошибкой")
    void transferFail() {
        TransferRequestDto transferAccountsDto = new TransferRequestDto();
        transferAccountsDto.setLoginFrom("login20");
        transferAccountsDto.setBalanceFrom(BigDecimal.ZERO);
        transferAccountsDto.setLoginTo("login21");
        transferAccountsDto.setBalanceTo(BigDecimal.ONE);
        assertThatThrownBy(() -> accountService.transfer(transferAccountsDto))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }
}