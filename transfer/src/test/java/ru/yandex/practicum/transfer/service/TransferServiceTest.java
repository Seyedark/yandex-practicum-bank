package ru.yandex.practicum.transfer.service;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import ru.yandex.practicum.transfer.SpringBootPostgreSQLBase;
import ru.yandex.practicum.transfer.TestSecurityConfig;
import ru.yandex.practicum.transfer.dao.repository.NotificationRepository;
import ru.yandex.practicum.transfer.dto.BlockDto;
import ru.yandex.practicum.transfer.dto.ConvertResponseDto;
import ru.yandex.practicum.transfer.dto.TransferAccountsDto;
import ru.yandex.practicum.transfer.dto.TransferFrontRequestDto;
import ru.yandex.practicum.transfer.enums.CurrencyEnum;
import ru.yandex.practicum.transfer.exception.TransferCustomException;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Import(TestSecurityConfig.class)
@DisplayName("Класс для проверки взаимодействия с сервисом зачисления/списания и с базой")
public class TransferServiceTest extends SpringBootPostgreSQLBase {

    @Autowired
    MeterRegistry registry;

    @Autowired
    TransferService transferService;

    @SpyBean
    NotificationRepository notificationRepository;

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    private OAuth2AuthorizedClientService authorizedClientService;

    @MockBean
    private RestCallerService restCallerService;

    @Test
    @DisplayName("Успешный перевод")
    void transferSuccess() {
        TransferFrontRequestDto transferFrontRequestDto = new TransferFrontRequestDto();
        transferFrontRequestDto.setLoginFrom("login1");
        transferFrontRequestDto.setLoginTo("login2");
        transferFrontRequestDto.setCurrencyFrom(CurrencyEnum.RUB.name());
        transferFrontRequestDto.setCurrencyTo(CurrencyEnum.RUB.name());
        transferFrontRequestDto.setTransferAmount(BigDecimal.ONE);

        TransferAccountsDto transferAccountsDto = new TransferAccountsDto();
        transferAccountsDto.setLoginTo("login2");
        transferAccountsDto.setBalanceTo(BigDecimal.ZERO);
        transferAccountsDto.setEmailTo("login2@mail.ru");
        transferAccountsDto.setLoginFrom("login1");
        transferAccountsDto.setBalanceFrom(BigDecimal.ONE);
        transferAccountsDto.setEmailFrom("login1@mail.ru");

        BlockDto blockDto = new BlockDto();
        blockDto.setBlocked(false);

        ConvertResponseDto convertResponseDto = new ConvertResponseDto();
        convertResponseDto.setConvertedAmount(BigDecimal.ONE);
        when(restCallerService.convert(any())).thenReturn(convertResponseDto);
        when(restCallerService.getBlock()).thenReturn(blockDto);
        when(restCallerService.getTransferAccountsDto(any(), any(), any(), any())).thenReturn(transferAccountsDto);

        transferService.transfer(transferFrontRequestDto);

        verify(restCallerService, times(1)).getTransferAccountsDto(any(), any(), any(), any());
        verify(restCallerService, times(1)).transfer(any());
        verify(notificationRepository, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("Перевод с ошибкой")
    void transferFail() {
        TransferFrontRequestDto transferFrontRequestDto = new TransferFrontRequestDto();
        transferFrontRequestDto.setLoginFrom("login1");
        transferFrontRequestDto.setLoginTo("login2");
        transferFrontRequestDto.setCurrencyFrom(CurrencyEnum.RUB.name());
        transferFrontRequestDto.setCurrencyTo(CurrencyEnum.RUB.name());
        transferFrontRequestDto.setTransferAmount(BigDecimal.TWO);

        TransferAccountsDto transferAccountsDto = new TransferAccountsDto();
        transferAccountsDto.setLoginTo("login2");
        transferAccountsDto.setBalanceTo(BigDecimal.ZERO);
        transferAccountsDto.setEmailTo("login2@mail.ru");
        transferAccountsDto.setLoginFrom("login1");
        transferAccountsDto.setBalanceFrom(BigDecimal.ONE);
        transferAccountsDto.setEmailFrom("login1@mail.ru");

        BlockDto blockDto = new BlockDto();
        blockDto.setBlocked(false);

        when(restCallerService.getBlock()).thenReturn(blockDto);
        when(restCallerService.getTransferAccountsDto(any(), any(), any(), any())).thenReturn(transferAccountsDto);

        assertThatThrownBy(() -> transferService.transfer(transferFrontRequestDto))
                .isInstanceOf(TransferCustomException.class);

        verify(restCallerService, times(1)).getTransferAccountsDto(any(), any(), any(), any());
        verify(restCallerService, times(0)).transfer(any());
        verify(notificationRepository, times(0)).saveAll(any());
    }
}