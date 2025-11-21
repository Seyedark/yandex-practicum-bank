package ru.yandex.practicum.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.account.dao.entity.AccountEntity;
import ru.yandex.practicum.account.dao.entity.NotificationEntity;
import ru.yandex.practicum.account.dao.repository.AccountRepository;
import ru.yandex.practicum.account.dto.*;
import ru.yandex.practicum.account.enums.AccountErrorEnum;
import ru.yandex.practicum.account.enums.MessageEnum;
import ru.yandex.practicum.account.exception.AccountCustomException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);


    public AccountDto findAccountByLogin(String login) {
        AccountEntity accountEntity = accountRepository.findByLogin(login);
        if (accountEntity != null) {
            return mapAccountEntityToAccountDto(accountEntity);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public AccountWithUsersDto findAccountByLoginWithUsers(String login) {
        List<AccountEntity> accountEntityList = accountRepository.findAll();
        AccountEntity accountEntity = getActualAccountEntity(accountEntityList, login);
        if (!accountEntityList.isEmpty() && accountEntity != null) {
            AccountWithUsersDto accountWithUsersDto = new AccountWithUsersDto();
            accountWithUsersDto.setLogin(accountEntity.getLogin());
            accountWithUsersDto.setPassword(accountEntity.getPassword());
            accountWithUsersDto.setFirstName(accountEntity.getFirstName());
            accountWithUsersDto.setLastName(accountEntity.getLastName());
            accountWithUsersDto.setEmail(accountEntity.getEmail());
            accountWithUsersDto.setBirthDate(accountEntity.getBirthDate());
            accountWithUsersDto.setBalance(accountEntity.getBalance());
            List<AccountEntity> otherUsers = accountEntityList.stream()
                    .filter(entity -> !entity.getId().equals(accountEntity.getId()))
                    .toList();
            fillShortAccountDtoList(accountWithUsersDto, otherUsers);
            return accountWithUsersDto;
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private AccountDto mapAccountEntityToAccountDto(AccountEntity accountEntity) {
        AccountDto accountDto = new AccountDto();
        accountDto.setLogin(accountEntity.getLogin());
        accountDto.setFirstName(accountEntity.getFirstName());
        accountDto.setLastName(accountEntity.getLastName());
        accountDto.setEmail(accountEntity.getEmail());
        accountDto.setBirthDate(accountEntity.getBirthDate());
        accountDto.setPassword(accountEntity.getPassword());
        accountDto.setBalance(accountEntity.getBalance());
        return accountDto;
    }

    @Transactional
    public void createAccount(AccountCreateRequestDto accountCreateRequestDto) {
        AccountEntity accountEntity = accountRepository.findByLogin(accountCreateRequestDto.getLogin());
        List<String> errorTypeList = new ArrayList<>();
        if (accountEntity == null) {
            checkAccountCreateRequestDto(accountCreateRequestDto, errorTypeList);
            if (errorTypeList.isEmpty()) {
                AccountEntity newAccountEntity = new AccountEntity();
                newAccountEntity.setLogin(accountCreateRequestDto.getLogin());
                newAccountEntity.setFirstName(accountCreateRequestDto.getFirstName());
                newAccountEntity.setLastName(accountCreateRequestDto.getLastName());
                newAccountEntity.setEmail(accountCreateRequestDto.getEmail());
                newAccountEntity.setBirthDate(accountCreateRequestDto.getBirthDate());
                newAccountEntity.setPassword(accountCreateRequestDto.getPassword());
                newAccountEntity.setBalance(BigDecimal.ZERO);

                NotificationEntity notificationEntity = new NotificationEntity();
                notificationEntity.setEmail(accountCreateRequestDto.getEmail());
                notificationEntity.setMessage(MessageEnum.CREATE_ACCOUNT.getMessage());
                notificationEntity.setNotificationSent(false);

                newAccountEntity.getNotifications().add(notificationEntity);

                accountRepository.save(newAccountEntity);
            } else {
                throw new AccountCustomException(errorTypeList);
            }
        } else {
            checkAccountCreateRequestDto(accountCreateRequestDto, errorTypeList);
            errorTypeList.add(AccountErrorEnum.LOGIN_ALREADY_EXIST.getMessage());
            throw new AccountCustomException(errorTypeList);
        }
    }

    @Transactional
    public void changePassword(AccountPasswordChangeDto accountPasswordChangeDto) {
        AccountEntity accountEntity = accountRepository.findByLogin(accountPasswordChangeDto.getLogin());
        List<String> errorTypeList = new ArrayList<>();
        if (accountEntity != null) {
            checkAccountPasswordChangeDto(accountPasswordChangeDto, errorTypeList);
            if (errorTypeList.isEmpty()) {
                accountEntity.setPassword(accountPasswordChangeDto.getPassword());

                NotificationEntity notificationEntity = new NotificationEntity();
                notificationEntity.setEmail(accountEntity.getEmail());
                notificationEntity.setMessage(MessageEnum.PASSWORD_CHANGE.getMessage());
                notificationEntity.setNotificationSent(false);

                accountEntity.getNotifications().add(notificationEntity);

                accountRepository.save(accountEntity);
            } else {
                throw new AccountCustomException(errorTypeList);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void changeInfo(AccountInfoChangeDto accountInfoChangeDto) {
        AccountEntity accountEntity = accountRepository.findByLogin(accountInfoChangeDto.getLogin());
        List<String> errorTypeList = new ArrayList<>();
        if (accountEntity != null) {
            checkAccountInfoChangeDto(accountInfoChangeDto, errorTypeList);
            if (errorTypeList.isEmpty()) {
                accountEntity.setFirstName(accountInfoChangeDto.getFirstName());
                accountEntity.setLastName(accountInfoChangeDto.getLastName());
                accountEntity.setBirthDate(accountInfoChangeDto.getBirthDate());

                accountRepository.save(accountEntity);
            } else {
                throw new AccountCustomException(errorTypeList);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void changeBalance(ChangeAccountBalanceRequestDto changeAccountBalanceRequestDto) {
        AccountEntity accountEntity = accountRepository.findByLogin(changeAccountBalanceRequestDto.getLogin());
        if (accountEntity != null) {
            accountEntity.setBalance(changeAccountBalanceRequestDto.getBalance());
            accountRepository.save(accountEntity);
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public TransferAccountsDto getTransferAccountsDto(String loginFrom, String loginTo) {
        AccountEntity accountFromEntity = accountRepository.findByLogin(loginFrom);
        AccountEntity accountToEntity = accountRepository.findByLogin(loginTo);
        if (accountFromEntity != null && accountToEntity != null) {
            TransferAccountsDto transferAccountsDto = new TransferAccountsDto();
            transferAccountsDto.setLoginFrom(accountFromEntity.getLogin());
            transferAccountsDto.setEmailFrom(accountFromEntity.getEmail());
            transferAccountsDto.setBalanceFrom(accountFromEntity.getBalance());
            transferAccountsDto.setLoginTo(accountToEntity.getLogin());
            transferAccountsDto.setEmailTo(accountToEntity.getEmail());
            transferAccountsDto.setBalanceTo(accountToEntity.getBalance());
            return transferAccountsDto;
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void transfer(TransferRequestDto transferRequestDto) {
        AccountEntity accountFromEntity = accountRepository.findByLogin(transferRequestDto.getLoginFrom());
        AccountEntity accountToEntity = accountRepository.findByLogin(transferRequestDto.getLoginTo());
        if (accountFromEntity != null && accountToEntity != null) {
            List<AccountEntity> accountEntityList = new ArrayList<>();
            accountFromEntity.setBalance(transferRequestDto.getBalanceFrom());
            accountToEntity.setBalance(transferRequestDto.getBalanceTo());
            accountRepository.saveAll(accountEntityList);
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private AccountEntity getActualAccountEntity(List<AccountEntity> accountEntityList, String login) {
        return accountEntityList.stream()
                .filter(acc -> login.equals(acc.getLogin()))
                .findFirst().orElse(null);
    }

    private void fillShortAccountDtoList(AccountWithUsersDto accountWithUsersDto, List<AccountEntity> accountEntityList) {
        if (accountEntityList.isEmpty()) {
            accountWithUsersDto.setShortAccountDtoList(null);
        } else {
            List<ShortAccountDto> shortAccountDtoLists = new ArrayList<>();
            accountEntityList.forEach(accountEntity -> {
                ShortAccountDto shortAccountDto = new ShortAccountDto();
                shortAccountDto.setLogin(accountEntity.getLogin());
                shortAccountDto.setFirstName(accountEntity.getFirstName());
                shortAccountDto.setLastName(accountEntity.getLastName());
                shortAccountDtoLists.add(shortAccountDto);
            });
            accountWithUsersDto.setShortAccountDtoList(shortAccountDtoLists);
        }
    }

    private void checkAccountCreateRequestDto(AccountCreateRequestDto accountCreateRequestDto, List<String> errorTypeList) {
        if (checkLength(accountCreateRequestDto.getLogin())) {
            errorTypeList.add(AccountErrorEnum.LOGIN.getMessage());
        }
        if (checkLength(accountCreateRequestDto.getFirstName())) {
            errorTypeList.add(AccountErrorEnum.FIRST_NAME.getMessage());
        }
        if (checkLength(accountCreateRequestDto.getLastName())) {
            errorTypeList.add(AccountErrorEnum.LAST_NAME.getMessage());
        }
        if (!isValidEmail(accountCreateRequestDto.getEmail())) {
            errorTypeList.add(AccountErrorEnum.EMAIL_FORMAT.getMessage());
        }
        if (checkLength(accountCreateRequestDto.getPassword())) {
            errorTypeList.add(AccountErrorEnum.PASSWORD.getMessage());
        }
        if (!isOlderThan18Years(accountCreateRequestDto.getBirthDate())) {
            errorTypeList.add(AccountErrorEnum.AGE.getMessage());
        }
    }

    private void checkAccountPasswordChangeDto(AccountPasswordChangeDto accountPasswordChangeDto, List<String> errorTypeList) {
        if (checkLength(accountPasswordChangeDto.getPassword())) {
            errorTypeList.add(AccountErrorEnum.PASSWORD.getMessage());
        }
    }

    private void checkAccountInfoChangeDto(AccountInfoChangeDto accountInfoChangeDto, List<String> errorTypeList) {
        if (checkLength(accountInfoChangeDto.getFirstName())) {
            errorTypeList.add(AccountErrorEnum.FIRST_NAME.getMessage());
        }
        if (checkLength(accountInfoChangeDto.getLastName())) {
            errorTypeList.add(AccountErrorEnum.LAST_NAME.getMessage());
        }
        if (!isOlderThan18Years(accountInfoChangeDto.getBirthDate())) {
            errorTypeList.add(AccountErrorEnum.AGE.getMessage());
        }
    }


    private boolean checkLength(String value) {
        return value.trim().length() < 4;
    }

    private static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    private boolean isOlderThan18Years(LocalDate birthdate) {
        if (birthdate == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        Period age = Period.between(birthdate, today);
        return age.getYears() > 18;
    }
}