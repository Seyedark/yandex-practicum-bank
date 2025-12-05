package ru.yandex.practicum.blocker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.blocker.dto.BlockDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockerService {

    public BlockDto getBlock() {
        BlockDto blockDto = new BlockDto();
        boolean blocked = Math.random() < 0.5;
        blockDto.setBlocked(blocked);
        return blockDto;
    }
}
