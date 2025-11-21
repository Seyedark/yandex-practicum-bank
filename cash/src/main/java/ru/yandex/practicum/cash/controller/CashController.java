package ru.yandex.practicum.cash.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cash.dto.ChangeAccountBalanceFrontRequestDto;
import ru.yandex.practicum.cash.exception.CashCustomException;
import ru.yandex.practicum.cash.service.CashService;

import java.util.List;

@RestController
@RequestMapping("/cash")
@RequiredArgsConstructor
public class CashController {

    private final CashService cashService;

    @PatchMapping
    @PreAuthorize("hasAuthority('cash_client')")
    public ResponseEntity<Void> changeAccountBalance(@RequestBody ChangeAccountBalanceFrontRequestDto changeAccountBalanceFrontRequestDto) {
        cashService.changeAccountBalance(changeAccountBalanceFrontRequestDto);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(CashCustomException.class)
    public ResponseEntity<List<String>> handleCustomException(CashCustomException accountCustomException) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(accountCustomException.getAdditionalField());
    }
}