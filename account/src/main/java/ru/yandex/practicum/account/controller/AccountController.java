package ru.yandex.practicum.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.account.dto.*;
import ru.yandex.practicum.account.exception.AccountCustomException;
import ru.yandex.practicum.account.service.AccountService;

import java.util.List;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    @PreAuthorize("hasAuthority('account_client')")
    public ResponseEntity<AccountDto> findAccountByLogin(@RequestParam(name = "login") String login) {
        return ResponseEntity.ok(accountService.findAccountByLogin(login));
    }

    @GetMapping("/full")
    @PreAuthorize("hasAuthority('account_client')")
    public ResponseEntity<AccountWithUsersDto> findAccountByLoginWithUsers(@RequestParam(name = "login") String login) {
        return ResponseEntity.ok(accountService.findAccountByLoginWithUsers(login));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('account_client')")
    public ResponseEntity<Void> createAccount(@RequestBody AccountCreateRequestDto accountCreateRequestDto) {
        accountService.createAccount(accountCreateRequestDto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/password")
    @PreAuthorize("hasAuthority('account_client')")
    public ResponseEntity<Void> changePassword(@RequestBody AccountPasswordChangeDto accountPasswordChangeDto) {
        accountService.changePassword(accountPasswordChangeDto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/info")
    @PreAuthorize("hasAuthority('account_client')")
    public ResponseEntity<Void> changeInfo(@RequestBody AccountInfoChangeDto accountInfoChangeDto) {
        accountService.changeInfo(accountInfoChangeDto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/balance")
    @PreAuthorize("hasAuthority('account_client')")
    public ResponseEntity<Void> changeBalance(@RequestBody ChangeAccountBalanceRequestDto changeAccountBalanceRequestDto) {
        accountService.changeBalance(changeAccountBalanceRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/transfer")
    @PreAuthorize("hasAuthority('account_client')")
    public ResponseEntity<TransferAccountsDto> transfer(@RequestParam(name = "loginFrom") String loginFrom,
                                                        @RequestParam(name = "loginTo") String loginTo) {
        return ResponseEntity.ok(accountService.getTransferAccountsDto(loginFrom, loginTo));
    }

    @PatchMapping("/transfer")
    @PreAuthorize("hasAuthority('account_client')")
    public ResponseEntity<Void> transfer(@RequestBody TransferRequestDto transferRequestDto) {
        accountService.transfer(transferRequestDto);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(AccountCustomException.class)
    public ResponseEntity<List<String>> handleCustomException(AccountCustomException accountCustomException) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(accountCustomException.getAdditionalField());
    }
}