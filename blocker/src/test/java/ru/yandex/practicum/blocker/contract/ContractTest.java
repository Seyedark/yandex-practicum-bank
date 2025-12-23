package ru.yandex.practicum.blocker.contract;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.blocker.dto.BlockDto;
import ru.yandex.practicum.blocker.service.BlockerService;

import static org.mockito.Mockito.when;

@WebMvcTest
public abstract class ContractTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    BlockerService blockerService;

    @BeforeEach
    public void setup() {
        BlockDto blockDto = new BlockDto();
        blockDto.setBlocked(true);
        when(blockerService.getBlock()).thenReturn(blockDto);
        io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc(
                MockMvcBuilders.webAppContextSetup(context).build());
    }
}