package ru.yandex.practicum.cash.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cash.dao.entity.NotificationEntity;
import ru.yandex.practicum.cash.dao.repository.NotificationRepository;
import ru.yandex.practicum.cash.dto.AccountDto;
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

    @Transactional
    public void changeAccountBalance(ChangeAccountBalanceFrontRequestDto changeAccountBalanceFrontRequestDto) {
        AccountDto accountDto = restCallerService.getAccount(changeAccountBalanceFrontRequestDto.getLogin());
        NotificationEntity notificationEntity = new NotificationEntity();

        notificationEntity.setEmail(accountDto.getEmail());
        notificationEntity.setNotificationSent(false);
        if (changeAccountBalanceFrontRequestDto.getActionEnum().equals(ActionEnum.ACCRUAL)) {
            notificationEntity.setMessage(MessageEnum.ACCRUAL.getMessage().formatted(changeAccountBalanceFrontRequestDto.getChangeAmount()));
            notificationRepository.save(notificationEntity);

            ChangeAccountBalanceRequestDto changeAccountBalanceRequestDto = new ChangeAccountBalanceRequestDto();
            changeAccountBalanceRequestDto.setLogin(accountDto.getLogin());
            changeAccountBalanceRequestDto.setBalance(changeAccountBalanceFrontRequestDto.getChangeAmount());

            restCallerService.changeBalance(changeAccountBalanceRequestDto);
        } else {
            if (checkBalance(accountDto.getBalance(), changeAccountBalanceFrontRequestDto.getChangeAmount())) {
                notificationEntity.setMessage(MessageEnum.WRITE_OFF.getMessage().formatted(changeAccountBalanceFrontRequestDto.getChangeAmount()));
                notificationRepository.save(notificationEntity);

                ChangeAccountBalanceRequestDto changeAccountBalanceRequestDto = new ChangeAccountBalanceRequestDto();
                changeAccountBalanceRequestDto.setLogin(accountDto.getLogin());
                changeAccountBalanceRequestDto.setBalance(accountDto.getBalance()
                        .subtract(changeAccountBalanceFrontRequestDto.getChangeAmount()));

                restCallerService.changeBalance(changeAccountBalanceRequestDto);
            } else {
                List<String> errorTypeList = new ArrayList<>();
                errorTypeList.add(CashErrorEnum.BALANCE_ERROR.getMessage()
                        .formatted(changeAccountBalanceFrontRequestDto.getChangeAmount(),
                                accountDto.getBalance()));
                throw new CashCustomException(errorTypeList);
            }
        }
    }

    private boolean checkBalance(BigDecimal balance, BigDecimal changeAmount) {
        return balance.compareTo(changeAmount) >= 0;
    }
}