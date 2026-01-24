package ru.yandex.practicum.cash.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cash.dao.entity.NotificationEntity;
import ru.yandex.practicum.cash.dao.repository.NotificationRepository;
import ru.yandex.practicum.cash.dto.BalanceDto;
import ru.yandex.practicum.cash.dto.BlockDto;
import ru.yandex.practicum.cash.dto.ChangeAccountBalanceFrontRequestDto;
import ru.yandex.practicum.cash.dto.ChangeAccountBalanceRequestDto;
import ru.yandex.practicum.cash.enums.ActionEnum;
import ru.yandex.practicum.cash.enums.CashErrorEnum;
import ru.yandex.practicum.cash.enums.MessageEnum;
import ru.yandex.practicum.cash.exception.CashCustomException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CashService {

    private final NotificationRepository notificationRepository;
    private final RestCallerService restCallerService;
    private final MetricService metricService;

    @Transactional
    public void changeAccountBalance(ChangeAccountBalanceFrontRequestDto changeAccountBalanceFrontRequestDto) {

        BlockDto blockDto = restCallerService.getBlock();
        if (blockDto.isBlocked()) {
            List<String> errorTypeList = new ArrayList<>();
            errorTypeList.add(CashErrorEnum.BLOCK_ERROR.getMessage());
            throw new CashCustomException(errorTypeList);
        }
        BalanceDto balanceDto = restCallerService.getBalance(changeAccountBalanceFrontRequestDto.getLogin(),
                changeAccountBalanceFrontRequestDto.getCurrency());
        NotificationEntity notificationEntity = new NotificationEntity();

        notificationEntity.setEmail(balanceDto.getEmail());
        notificationEntity.setNotificationSent(false);
        if (changeAccountBalanceFrontRequestDto.getActionEnum().equals(ActionEnum.ACCRUAL)) {
            notificationEntity.setMessage(MessageEnum.ACCRUAL.getMessage().formatted(changeAccountBalanceFrontRequestDto.getChangeAmount()));
            notificationRepository.save(notificationEntity);

            ChangeAccountBalanceRequestDto changeAccountBalanceRequestDto = new ChangeAccountBalanceRequestDto();
            changeAccountBalanceRequestDto.setLogin(changeAccountBalanceFrontRequestDto.getLogin());
            changeAccountBalanceRequestDto.setCurrency(changeAccountBalanceFrontRequestDto.getCurrency());
            changeAccountBalanceRequestDto.setBalance(balanceDto.getBalance().add(changeAccountBalanceFrontRequestDto.getChangeAmount()));

            restCallerService.changeBalance(changeAccountBalanceRequestDto);
        } else {
            if (checkBalance(balanceDto.getBalance(), changeAccountBalanceFrontRequestDto.getChangeAmount())) {
                notificationEntity.setMessage(MessageEnum.WRITE_OFF.getMessage().formatted(changeAccountBalanceFrontRequestDto.getChangeAmount()));
                notificationRepository.save(notificationEntity);

                ChangeAccountBalanceRequestDto changeAccountBalanceRequestDto = new ChangeAccountBalanceRequestDto();
                changeAccountBalanceRequestDto.setLogin(changeAccountBalanceFrontRequestDto.getLogin());
                changeAccountBalanceRequestDto.setCurrency(changeAccountBalanceFrontRequestDto.getCurrency());
                changeAccountBalanceRequestDto.setBalance(balanceDto.getBalance()
                        .subtract(changeAccountBalanceFrontRequestDto.getChangeAmount()));

                restCallerService.changeBalance(changeAccountBalanceRequestDto);
            } else {
                List<String> errorTypeList = new ArrayList<>();
                errorTypeList.add(CashErrorEnum.BALANCE_ERROR.getMessage()
                        .formatted(changeAccountBalanceFrontRequestDto.getChangeAmount(),
                                balanceDto.getBalance()));

                metricService.failedChangeAccountBalance(changeAccountBalanceFrontRequestDto);
                throw new CashCustomException(errorTypeList);
            }
        }

    }

    private boolean checkBalance(BigDecimal balance, BigDecimal changeAmount) {
        return balance.compareTo(changeAmount) >= 0;
    }
}