package ru.yandex.practicum.transfer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.transfer.dto.TransferFrontRequestDto;
import ru.yandex.practicum.transfer.exception.TransferCustomException;
import ru.yandex.practicum.transfer.service.TransferService;

import java.util.List;

@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;

    @PatchMapping
    @PreAuthorize("hasAuthority('transfer_client')")
    public ResponseEntity<Void> changeAccountBalance(@RequestBody TransferFrontRequestDto transferFrontRequestDto) {
        transferService.transfer(transferFrontRequestDto);
        return ResponseEntity.ok().build();
    }


    @ExceptionHandler(TransferCustomException.class)
    public ResponseEntity<List<String>> handleCustomException(TransferCustomException accountCustomException) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(accountCustomException.getAdditionalField());
    }
}