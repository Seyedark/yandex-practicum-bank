package ru.yandex.practicum.transfer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.transfer.dao.entity.NotificationEntity;
import ru.yandex.practicum.transfer.dao.repository.NotificationRepository;
import ru.yandex.practicum.transfer.dto.*;
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
    private final MetricService metricService;

    @Transactional
    public void transfer(TransferFrontRequestDto transferFrontRequestDto) {
        BlockDto blockDto = restCallerService.getBlock();
        if (blockDto.isBlocked()) {
            List<String> errorTypeList = new ArrayList<>();
            errorTypeList.add(TransferErrorEnum.BLOCK_ERROR.getMessage());
            throw new TransferCustomException(errorTypeList);
        }
        TransferAccountsDto transferAccountsDto = restCallerService.getTransferAccountsDto(transferFrontRequestDto.getLoginFrom(),
                transferFrontRequestDto.getLoginTo(), transferFrontRequestDto.getCurrencyTo(), transferFrontRequestDto.getCurrencyFrom());
        if (checkBalance(transferAccountsDto.getBalanceFrom(), transferFrontRequestDto.getTransferAmount())) {


            ConvertRequestDto convertRequestDto = new ConvertRequestDto();
            convertRequestDto.setCurrencyTo(transferFrontRequestDto.getCurrencyTo());
            convertRequestDto.setCurrencyFrom(transferFrontRequestDto.getCurrencyFrom());
            convertRequestDto.setConvertAmount(transferFrontRequestDto.getTransferAmount());

            ConvertResponseDto convertResponseDto = restCallerService.convert(convertRequestDto);

            notificationRepository.saveAll(formNotificationList(transferAccountsDto, transferFrontRequestDto.getTransferAmount()));

            TransferRequestDto transferRequestDto = new TransferRequestDto();
            transferRequestDto.setLoginFrom(transferAccountsDto.getLoginFrom());
            transferRequestDto.setBalanceFrom(transferAccountsDto.getBalanceFrom().subtract(transferFrontRequestDto.getTransferAmount()));
            transferRequestDto.setLoginTo(transferAccountsDto.getLoginTo());
            transferRequestDto.setBalanceTo(transferAccountsDto.getBalanceTo().add(convertResponseDto.getConvertedAmount()));
            transferRequestDto.setCurrencyFrom(transferFrontRequestDto.getCurrencyFrom());
            transferRequestDto.setCurrencyTo(transferFrontRequestDto.getCurrencyTo());

            restCallerService.transfer(transferRequestDto);
        } else {
            List<String> errorTypeList = new ArrayList<>();
            errorTypeList.add(TransferErrorEnum.BALANCE_ERROR.getMessage()
                    .formatted(transferFrontRequestDto.getTransferAmount(),
                            transferAccountsDto.getBalanceFrom()));
            metricService.failedTransfer(transferFrontRequestDto);
            throw new TransferCustomException(errorTypeList);
        }
    }

    public List<ExchangeDto> getExchangeDtoList() {
        return restCallerService.getExchangeDtoList();
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