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
import ru.yandex.practicum.cash.dto.BalanceDto;
import ru.yandex.practicum.cash.dto.BlockDto;
import ru.yandex.practicum.cash.dto.ChangeAccountBalanceFrontRequestDto;
import ru.yandex.practicum.cash.enums.ActionEnum;
import ru.yandex.practicum.cash.enums.CurrencyEnum;
import ru.yandex.practicum.cash.exception.CashCustomException;

import java.math.BigDecimal;

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
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setBalance(BigDecimal.ZERO);
        balanceDto.setEmail("test@mail.ru");

        ChangeAccountBalanceFrontRequestDto changeAccountBalanceFrontRequestDto = new ChangeAccountBalanceFrontRequestDto();
        changeAccountBalanceFrontRequestDto.setLogin("login");
        changeAccountBalanceFrontRequestDto.setCurrency(CurrencyEnum.RUB.name());
        changeAccountBalanceFrontRequestDto.setChangeAmount(BigDecimal.ONE);
        changeAccountBalanceFrontRequestDto.setActionEnum(ActionEnum.ACCRUAL);

        BlockDto blockDto = new BlockDto();
        blockDto.setBlocked(false);

        when(restCallerService.getBlock()).thenReturn(blockDto);
        when(restCallerService.getBalance(changeAccountBalanceFrontRequestDto.getLogin(), changeAccountBalanceFrontRequestDto.getCurrency())).thenReturn(balanceDto);

        cashService.changeAccountBalance(changeAccountBalanceFrontRequestDto);

        verify(restCallerService, times(1)).changeBalance(any());
        verify(notificationRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Успешное списание")
    void changeAccountBalanceWriteOffSuccess() {
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setBalance(BigDecimal.ONE);
        balanceDto.setEmail("test@mail.ru");

        ChangeAccountBalanceFrontRequestDto changeAccountBalanceFrontRequestDto = new ChangeAccountBalanceFrontRequestDto();
        changeAccountBalanceFrontRequestDto.setLogin("login");
        changeAccountBalanceFrontRequestDto.setCurrency(CurrencyEnum.RUB.name());
        changeAccountBalanceFrontRequestDto.setChangeAmount(BigDecimal.ONE);
        changeAccountBalanceFrontRequestDto.setActionEnum(ActionEnum.WRITE_OFF);
        BlockDto blockDto = new BlockDto();
        blockDto.setBlocked(false);

        when(restCallerService.getBlock()).thenReturn(blockDto);
        when(restCallerService.getBalance(changeAccountBalanceFrontRequestDto.getLogin(), changeAccountBalanceFrontRequestDto.getCurrency())).thenReturn(balanceDto);

        cashService.changeAccountBalance(changeAccountBalanceFrontRequestDto);

        verify(restCallerService, times(1)).changeBalance(any());
        verify(notificationRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Ошибка списания")
    void changeAccountBalanceWriteOffFail() {
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setBalance(BigDecimal.ZERO);
        balanceDto.setEmail("test@mail.ru");

        ChangeAccountBalanceFrontRequestDto changeAccountBalanceFrontRequestDto = new ChangeAccountBalanceFrontRequestDto();
        changeAccountBalanceFrontRequestDto.setLogin("login");
        changeAccountBalanceFrontRequestDto.setCurrency(CurrencyEnum.RUB.name());
        changeAccountBalanceFrontRequestDto.setChangeAmount(BigDecimal.ONE);
        changeAccountBalanceFrontRequestDto.setActionEnum(ActionEnum.WRITE_OFF);
        BlockDto blockDto = new BlockDto();
        blockDto.setBlocked(false);

        when(restCallerService.getBlock()).thenReturn(blockDto);
        when(restCallerService.getBalance(changeAccountBalanceFrontRequestDto.getLogin(), changeAccountBalanceFrontRequestDto.getCurrency())).thenReturn(balanceDto);

        assertThatThrownBy(() -> cashService.changeAccountBalance(changeAccountBalanceFrontRequestDto))
                .isInstanceOf(CashCustomException.class);
    }
}
