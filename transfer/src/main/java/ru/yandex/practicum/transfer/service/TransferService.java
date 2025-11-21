package ru.yandex.practicum.transfer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.transfer.dao.entity.NotificationEntity;
import ru.yandex.practicum.transfer.dao.repository.NotificationRepository;
import ru.yandex.practicum.transfer.dto.TransferAccountsDto;
import ru.yandex.practicum.transfer.dto.TransferFrontRequestDto;
import ru.yandex.practicum.transfer.dto.TransferRequestDto;
import ru.yandex.practicum.transfer.enums.MessageEnum;
import ru.yandex.practicum.transfer.enums.TransferErrorEnum;
import ru.yandex.practicum.transfer.exception.TransferCustomException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {

    private final NotificationRepository notificationRepository;
    private final RestCallerService restCallerService;

    @Transactional
    public void transfer(TransferFrontRequestDto transferFrontRequestDto) {
        TransferAccountsDto transferAccountsDto = restCallerService.getTransferAccountsDto(transferFrontRequestDto.getLoginFrom(),
                transferFrontRequestDto.getLoginTo());
        if (checkBalance(transferAccountsDto.getBalanceFrom(), transferFrontRequestDto.getTransferAmount())) {
            notificationRepository.saveAll(formNotificationList(transferAccountsDto, transferFrontRequestDto.getTransferAmount()));
            TransferRequestDto transferRequestDto = new TransferRequestDto();
            transferRequestDto.setLoginFrom(transferAccountsDto.getLoginFrom());
            transferRequestDto.setBalanceFrom(transferAccountsDto.getBalanceFrom().subtract(transferFrontRequestDto.getTransferAmount()));
            transferRequestDto.setLoginTo(transferAccountsDto.getLoginTo());
            transferRequestDto.setBalanceTo(transferAccountsDto.getBalanceFrom().add(transferFrontRequestDto.getTransferAmount()));
            restCallerService.transfer(transferRequestDto);
        } else {
            List<String> errorTypeList = new ArrayList<>();
            errorTypeList.add(TransferErrorEnum.BALANCE_ERROR.getMessage()
                    .formatted(transferFrontRequestDto.getTransferAmount(),
                            transferAccountsDto.getBalanceFrom()));
            throw new TransferCustomException(errorTypeList);
        }
    }

    private boolean checkBalance(BigDecimal balance, BigDecimal changeAmount) {
        return balance.compareTo(changeAmount) >= 0;
    }

    private List<NotificationEntity> formNotificationList(TransferAccountsDto transferAccountsDto, BigDecimal transferAmount) {
        List<NotificationEntity> notificationEntityList = new ArrayList<>();

        NotificationEntity notificationFromEntity = new NotificationEntity();
        notificationFromEntity.setEmail(transferAccountsDto.getEmailFrom());
        notificationFromEntity.setMessage(MessageEnum.WRITE_OFF.getMessage().formatted(transferAmount));
        notificationFromEntity.setNotificationSent(false);

        NotificationEntity notificationToEntity = new NotificationEntity();
        notificationToEntity.setEmail(transferAccountsDto.getEmailTo());
        notificationToEntity.setMessage(MessageEnum.ACCRUAL.getMessage().formatted(transferAmount));
        notificationToEntity.setNotificationSent(false);

        notificationEntityList.add(notificationFromEntity);
        notificationEntityList.add(notificationToEntity);
        return notificationEntityList;
    }
}