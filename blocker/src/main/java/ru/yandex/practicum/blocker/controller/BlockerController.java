package ru.yandex.practicum.blocker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.blocker.dto.BlockDto;
import ru.yandex.practicum.blocker.service.BlockerService;

@RestController
@RequestMapping("/blocker")
@RequiredArgsConstructor
public class BlockerController {

    private final BlockerService blockerService;

    @GetMapping("/block")
    @PreAuthorize("hasAuthority('blocker_client')")
    public ResponseEntity<BlockDto> getBlock() {
        return ResponseEntity.ok(blockerService.getBlock());
    }
}
