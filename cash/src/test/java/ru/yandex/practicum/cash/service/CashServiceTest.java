package ru.yandex.practicum.cash.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import ru.yandex.practicum.cash.SpringBootPostgreSQLBase;
import ru.yandex.practicum.cash.TestSecurityConfig;
import ru.yandex.practicum.cash.dao.repository.NotificationRepository;
import ru.yandex.practicum.cash.dto.AccountDto;
import ru.yandex.practicum.cash.dto.ChangeAccountBalanceFrontRequestDto;
import ru.yandex.practicum.cash.enums.ActionEnum;
import ru.yandex.practicum.cash.exception.CashCustomException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Import(TestSecurityConfig.class)
@DisplayName("Класс для проверки взаимодействия с сервисом зачисления/списания и с базой")
public class CashServiceTest extends SpringBootPostgreSQLBase {

    @Autowired
    CashService cashService;

    @SpyBean
    NotificationRepository notificationRepository;

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    private OAuth2AuthorizedClientService authorizedClientService;

    @MockBean
    private RestCallerService restCallerService;

    @BeforeEach
    void cleanup() {
        notificationRepository.deleteAll();
    }

    @Test
    @DisplayName("Успешное зачисление")
    void changeAccountBalanceAccrualSuccess() {
        AccountDto accountDto = new AccountDto();
        accountDto.setLogin("login");
        accountDto.setFirstName("Тест");
        accountDto.setLastName("Тестов");
        accountDto.setEmail("test@mail.ru");
        accountDto.setBirthDate(LocalDate.now().minusYears(20));
        accountDto.setPassword("12345");
        accountDto.setBalance(BigDecimal.ZERO);

        ChangeAccountBalanceFrontRequestDto changeAccountBalanceFrontRequestDto = new ChangeAccountBalanceFrontRequestDto();
        changeAccountBalanceFrontRequestDto.setLogin("login");
        changeAccountBalanceFrontRequestDto.setChangeAmount(BigDecimal.ONE);
        changeAccountBalanceFrontRequestDto.setActionEnum(ActionEnum.ACCRUAL);

        when(restCallerService.getAccount(changeAccountBalanceFrontRequestDto.getLogin())).thenReturn(accountDto);

        cashService.changeAccountBalance(changeAccountBalanceFrontRequestDto);


        verify(restCallerService, times(1)).changeBalance(any());
        verify(notificationRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Успешное списание")
    void changeAccountBalanceWriteOffSuccess() {
        AccountDto accountDto = new AccountDto();
        accountDto.setLogin("login");
        accountDto.setFirstName("Тест");
        accountDto.setLastName("Тестов");
        accountDto.setEmail("test@mail.ru");
        accountDto.setBirthDate(LocalDate.now().minusYears(20));
        accountDto.setPassword("12345");
        accountDto.setBalance(BigDecimal.ONE);

        ChangeAccountBalanceFrontRequestDto changeAccountBalanceFrontRequestDto = new ChangeAccountBalanceFrontRequestDto();
        changeAccountBalanceFrontRequestDto.setLogin("login");
        changeAccountBalanceFrontRequestDto.setChangeAmount(BigDecimal.ONE);
        changeAccountBalanceFrontRequestDto.setActionEnum(ActionEnum.WRITE_OFF);

        when(restCallerService.getAccount(changeAccountBalanceFrontRequestDto.getLogin())).thenReturn(accountDto);

        cashService.changeAccountBalance(changeAccountBalanceFrontRequestDto);


        verify(restCallerService, times(1)).changeBalance(any());
        verify(notificationRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Ошибка списания")
    void changeAccountBalanceWriteOffFail() {
        AccountDto accountDto = new AccountDto();
        accountDto.setLogin("login");
        accountDto.setFirstName("Тест");
        accountDto.setLastName("Тестов");
        accountDto.setEmail("test@mail.ru");
        accountDto.setBirthDate(LocalDate.now().minusYears(20));
        accountDto.setPassword("12345");
        accountDto.setBalance(BigDecimal.ZERO);

        ChangeAccountBalanceFrontRequestDto changeAccountBalanceFrontRequestDto = new ChangeAccountBalanceFrontRequestDto();
        changeAccountBalanceFrontRequestDto.setLogin("login");
        changeAccountBalanceFrontRequestDto.setChangeAmount(BigDecimal.ONE);
        changeAccountBalanceFrontRequestDto.setActionEnum(ActionEnum.WRITE_OFF);

        when(restCallerService.getAccount(changeAccountBalanceFrontRequestDto.getLogin())).thenReturn(accountDto);

        assertThatThrownBy(() ->  cashService.changeAccountBalance(changeAccountBalanceFrontRequestDto))
                .isInstanceOf(CashCustomException.class);
    }
}
