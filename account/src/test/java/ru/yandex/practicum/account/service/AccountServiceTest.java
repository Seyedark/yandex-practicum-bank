package ru.yandex.practicum.account.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.account.SpringBootPostgreSQLBase;
import ru.yandex.practicum.account.TestSecurityConfig;
import ru.yandex.practicum.account.dao.entity.AccountEntity;
import ru.yandex.practicum.account.dao.repository.AccountRepository;
import ru.yandex.practicum.account.dto.*;
import ru.yandex.practicum.account.exception.AccountCustomException;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    private OAuth2AuthorizedClientService authorizedClientService;

    @MockBean
    private RestCallerService restCallerService;

    @BeforeEach
    void cleanup() {
        accountRepository.deleteAll();
    }


    @Test
    @DisplayName("Пользователь найден по логину")
    void findAccountByLoginSuccess() {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setLogin("login");
        accountEntity.setFirstName("Тест");
        accountEntity.setLastName("Тестов");
        accountEntity.setEmail("test@mail.ru");
        accountEntity.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity.setPassword("12345");
        accountEntity.setBalance(BigDecimal.ZERO);
        accountRepository.save(accountEntity);

        AccountDto result = accountService.findAccountByLogin("login");

        assertNotNull(result);
        assertEquals("login", result.getLogin());
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
        accountEntity1.setLogin("login");
        accountEntity1.setFirstName("Тест");
        accountEntity1.setLastName("Тестов");
        accountEntity1.setEmail("test@mail.ru");
        accountEntity1.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity1.setPassword("12345");
        accountEntity1.setBalance(BigDecimal.ZERO);
        accountRepository.save(accountEntity1);

        AccountEntity accountEntity2 = new AccountEntity();
        accountEntity2.setLogin("login1");
        accountEntity2.setFirstName("Тест1");
        accountEntity2.setLastName("Тестов");
        accountEntity2.setEmail("test@mail.ru");
        accountEntity2.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity2.setPassword("12345");
        accountEntity2.setBalance(BigDecimal.ZERO);
        accountRepository.save(accountEntity2);

        AccountWithUsersDto accountWithUsersDto = accountService.findAccountByLoginWithUsers("login");


        assertNotNull(accountWithUsersDto);
        assertEquals("login", accountWithUsersDto.getLogin());
        assertEquals("Тест", accountWithUsersDto.getFirstName());
        assertEquals("Тестов", accountWithUsersDto.getLastName());
        assertEquals("test@mail.ru", accountWithUsersDto.getEmail());
        assertEquals("Тест1", accountWithUsersDto.getShortAccountDtoList().getFirst().getFirstName());
    }

    @Test
    @DisplayName("Пользователь и связанные пользователи не найдены")
    void findAccountByLoginWithUsersFail() {
        assertThatThrownBy(() -> accountService.findAccountByLoginWithUsers("login"))
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
        accountCreateRequestDto.setLogin("login");
        accountCreateRequestDto.setFirstName("Тест");
        accountCreateRequestDto.setLastName("Тестов");
        accountCreateRequestDto.setEmail("test@mail.ru");
        accountCreateRequestDto.setBirthDate(LocalDate.now().minusYears(20));
        accountCreateRequestDto.setPassword("12345");

        accountService.createAccount(accountCreateRequestDto);
        AccountEntity result = accountRepository.findByLogin("login");

        assertNotNull(result);
        assertEquals("login", result.getLogin());
        assertEquals("Тест", result.getFirstName());
        assertEquals("Тестов", result.getLastName());
        assertEquals("test@mail.ru", result.getEmail());
    }

    @Test
    @DisplayName("Ошибка создания нового аккаунта")
    void createAccountFail() {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setLogin("login");
        accountEntity.setFirstName("Тест");
        accountEntity.setLastName("Тестов");
        accountEntity.setEmail("test@mail.ru");
        accountEntity.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity.setPassword("12345");
        accountEntity.setBalance(BigDecimal.ZERO);
        accountRepository.save(accountEntity);

        AccountCreateRequestDto accountCreateRequestDto = new AccountCreateRequestDto();
        accountCreateRequestDto.setLogin("login");
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
        accountEntity.setLogin("login");
        accountEntity.setFirstName("Тест");
        accountEntity.setLastName("Тестов");
        accountEntity.setEmail("test@mail.ru");
        accountEntity.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity.setPassword("12345");
        accountEntity.setBalance(BigDecimal.ZERO);
        accountRepository.save(accountEntity);

        AccountPasswordChangeDto accountPasswordChangeDto = new AccountPasswordChangeDto();
        accountPasswordChangeDto.setLogin("login");
        accountPasswordChangeDto.setPassword("123456");

        accountService.changePassword(accountPasswordChangeDto);

        AccountEntity result = accountRepository.findByLogin("login");

        assertEquals("123456", result.getPassword());
    }

    @Test
    @DisplayName("Ошибка изменения пароля")
    void changePasswordFail() {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setLogin("login");
        accountEntity.setFirstName("Тест");
        accountEntity.setLastName("Тестов");
        accountEntity.setEmail("test@mail.ru");
        accountEntity.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity.setPassword("12345");
        accountEntity.setBalance(BigDecimal.ZERO);
        accountRepository.save(accountEntity);

        AccountPasswordChangeDto accountPasswordChangeDto = new AccountPasswordChangeDto();
        accountPasswordChangeDto.setLogin("login");
        accountPasswordChangeDto.setPassword("123");

        assertThatThrownBy(() -> accountService.changePassword(accountPasswordChangeDto))
                .isInstanceOf(AccountCustomException.class);
    }

    @Test
    @DisplayName("Смена информации об аккаунте")
    void changeInfoSuccess() {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setLogin("login");
        accountEntity.setFirstName("Тест");
        accountEntity.setLastName("Тестов");
        accountEntity.setEmail("test@mail.ru");
        accountEntity.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity.setPassword("12345");
        accountEntity.setBalance(BigDecimal.ZERO);
        accountRepository.save(accountEntity);

        AccountInfoChangeDto accountInfoChangeDto = new AccountInfoChangeDto();
        accountInfoChangeDto.setLogin("login");
        accountInfoChangeDto.setFirstName("Тест1");
        accountInfoChangeDto.setLastName("Тестов1");
        accountInfoChangeDto.setBirthDate(LocalDate.now().minusYears(21));

        accountService.changeInfo(accountInfoChangeDto);

        AccountEntity result = accountRepository.findByLogin("login");

        assertEquals("Тест1", result.getFirstName());
        assertEquals("Тестов1", result.getLastName());
        assertEquals(LocalDate.now().minusYears(21), result.getBirthDate());

    }


    @Test
    @DisplayName("Ошибка смены информации об аккаунта")
    void changeInfoFail() {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setLogin("login");
        accountEntity.setFirstName("Тест");
        accountEntity.setLastName("Тестов");
        accountEntity.setEmail("test@mail.ru");
        accountEntity.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity.setPassword("12345");
        accountEntity.setBalance(BigDecimal.ZERO);
        accountRepository.save(accountEntity);

        AccountInfoChangeDto accountInfoChangeDto = new AccountInfoChangeDto();
        accountInfoChangeDto.setLogin("login");
        accountInfoChangeDto.setFirstName("Т");
        accountInfoChangeDto.setLastName("Т");
        accountInfoChangeDto.setBirthDate(LocalDate.now().minusYears(1));

        assertThatThrownBy(() -> accountService.changeInfo(accountInfoChangeDto))
                .isInstanceOf(AccountCustomException.class);
    }

    @Test
    @DisplayName("Смена баланса")
    void changeBalanceSuccess() {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setLogin("login");
        accountEntity.setFirstName("Тест");
        accountEntity.setLastName("Тестов");
        accountEntity.setEmail("test@mail.ru");
        accountEntity.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity.setPassword("12345");
        accountEntity.setBalance(BigDecimal.ZERO);
        accountRepository.save(accountEntity);

        ChangeAccountBalanceRequestDto changeAccountBalanceRequestDto = new ChangeAccountBalanceRequestDto();
        changeAccountBalanceRequestDto.setLogin("login");
        changeAccountBalanceRequestDto.setBalance(BigDecimal.ONE);

        accountService.changeBalance(changeAccountBalanceRequestDto);

        AccountEntity result = accountRepository.findByLogin("login");

        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    @DisplayName("Ошибка смены баланса")
    void changeBalanceFail() {
        ChangeAccountBalanceRequestDto changeAccountBalanceRequestDto = new ChangeAccountBalanceRequestDto();
        changeAccountBalanceRequestDto.setLogin("login");
        changeAccountBalanceRequestDto.setBalance(BigDecimal.ONE);
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
        accountEntity1.setLogin("login");
        accountEntity1.setFirstName("Тест");
        accountEntity1.setLastName("Тестов");
        accountEntity1.setEmail("test@mail.ru");
        accountEntity1.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity1.setPassword("12345");
        accountEntity1.setBalance(BigDecimal.ZERO);
        accountRepository.save(accountEntity1);

        AccountEntity accountEntity2 = new AccountEntity();
        accountEntity2.setLogin("login1");
        accountEntity2.setFirstName("Тест1");
        accountEntity2.setLastName("Тестов");
        accountEntity2.setEmail("test@mail.ru");
        accountEntity2.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity2.setPassword("12345");
        accountEntity2.setBalance(BigDecimal.ZERO);
        accountRepository.save(accountEntity2);

        TransferAccountsDto transferAccountsDto = accountService.getTransferAccountsDto("login", "login1");
        assertNotNull(transferAccountsDto);
    }

    @Test
    @DisplayName("Ошибка получения аккаунтов для перевода")
    void getTransferAccountsDtoFail() {
        assertThatThrownBy(() -> accountService.getTransferAccountsDto("login", "login1"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }

    @Test
    @DisplayName("Перевод")
    void transferSuccess() {
        AccountEntity accountEntity1 = new AccountEntity();
        accountEntity1.setLogin("login");
        accountEntity1.setFirstName("Тест");
        accountEntity1.setLastName("Тестов");
        accountEntity1.setEmail("test@mail.ru");
        accountEntity1.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity1.setPassword("12345");
        accountEntity1.setBalance(BigDecimal.ONE);
        accountRepository.save(accountEntity1);

        AccountEntity accountEntity2 = new AccountEntity();
        accountEntity2.setLogin("login1");
        accountEntity2.setFirstName("Тест1");
        accountEntity2.setLastName("Тестов");
        accountEntity2.setEmail("test@mail.ru");
        accountEntity2.setBirthDate(LocalDate.now().minusYears(20));
        accountEntity2.setPassword("12345");
        accountEntity2.setBalance(BigDecimal.ZERO);
        accountRepository.save(accountEntity2);

        TransferRequestDto transferAccountsDto = new TransferRequestDto();
        transferAccountsDto.setLoginFrom("login");
        transferAccountsDto.setBalanceFrom(BigDecimal.ZERO);
        transferAccountsDto.setLoginTo("login1");
        transferAccountsDto.setBalanceTo(BigDecimal.ONE);
        accountService.transfer(transferAccountsDto);

        AccountEntity result1 = accountRepository.findByLogin("login");
        AccountEntity result2 = accountRepository.findByLogin("login1");

        assertThat(result1.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result2.getBalance()).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    @DisplayName("Перевод")
    void transferFail() {
        TransferRequestDto transferAccountsDto = new TransferRequestDto();
        transferAccountsDto.setLoginFrom("login");
        transferAccountsDto.setBalanceFrom(BigDecimal.ZERO);
        transferAccountsDto.setLoginTo("login1");
        transferAccountsDto.setBalanceTo(BigDecimal.ONE);
        assertThatThrownBy(() -> accountService.transfer(transferAccountsDto))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }
}