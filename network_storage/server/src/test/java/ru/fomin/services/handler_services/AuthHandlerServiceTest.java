package ru.fomin.services.handler_services;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import ru.fomin.core.MainHandler;
import ru.fomin.dto.requests.AuthRequest;
import ru.fomin.dto.responses.AuthResult;
import ru.fomin.services.db_services.UserService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


class AuthHandlerServiceTest {

    @Mock
    private UserService userService;

    @Mock
    ChannelHandlerContext ctx;

    @Mock
    ChannelPipeline channelPipeline;

    @Mock
    MainHandler handler;

    @Captor
    ArgumentCaptor<UserService> userServiceCaptor;

    @InjectMocks
    private AuthHandlerService authHandlerService;

    public AuthHandlerServiceTest() {
        MockitoAnnotations.initMocks(this);
    }

    @ParameterizedTest
    @CsvSource({
            "login, password, OK_AUTH",
            "login, 'wrong password', FAIL_AUTH",
    })
    public void authHandle_Authentication(String actualLogin, String actualPassword, String result) throws IOException {
        String login = "login";
        String password = "password";
        Mockito.when(userService.isValidUserData(Mockito.anyString(), Mockito.anyString())).thenReturn(new Object[]{false, 0});
        Mockito.when(userService.isValidUserData(login, password)).thenReturn(new Object[]{true, 1L});
        Mockito.when(ctx.pipeline()).thenReturn(channelPipeline);
        Mockito.when(channelPipeline.get(MainHandler.class)).thenReturn(handler);

        authHandlerService.authHandle(ctx, AuthRequest.RequestType.AUTH, actualLogin, actualPassword);

        if (result.equals("OK_AUTH")) {
            Mockito.verify(handler).setUserService(userServiceCaptor.capture());
            assertEquals(userService, userServiceCaptor.getValue());
        }
        Mockito.verify(ctx).writeAndFlush(new AuthResult(AuthResult.Result.valueOf(result)));
    }
}