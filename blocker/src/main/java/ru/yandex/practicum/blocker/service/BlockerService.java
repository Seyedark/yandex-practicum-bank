package ru.yandex.practicum.blocker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.blocker.dto.BlockDto;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockerService {

    public BlockDto getBlock() {
        BlockDto blockDto = new BlockDto();
        boolean blocked = ThreadLocalRandom.current().nextBoolean();
        blockDto.setBlocked(blocked);
        return blockDto;
    }
}
