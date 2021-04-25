package ru.fomin.services.handler_services;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import ru.fomin.core.MainHandler;
import ru.fomin.core.PropertiesLoader;
import ru.fomin.dto.responses.AuthResult;
import ru.fomin.dto.enumeration.AuthAndRegRequest;
import ru.fomin.dto.enumeration.AuthAndRegResult;
import ru.fomin.services.db_services.UserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AuthHandlerServiceTest {

    private static final String WRIGHT_LOGIN = "login";
    private static final String WRIGHT_PASSWORD = "password";

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

    @Captor
    ArgumentCaptor<AuthResult> authResult;

    @InjectMocks
    private AuthHandlerService authHandlerService;

    public AuthHandlerServiceTest() {
        MockitoAnnotations.initMocks(this);
    }

    @ParameterizedTest
    @CsvSource({
            WRIGHT_LOGIN + ", " + WRIGHT_PASSWORD + ", OK_AUTH",
            "login, 'wrong password', FAIL_AUTH",
    })
    public void authHandle_Authentication(String actualLogin, String actualPassword, String result) {
        Mockito.when(userService.isValidUserData(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(new Object[]{false, 0});
        Mockito.when(userService.isValidUserData(WRIGHT_LOGIN, WRIGHT_PASSWORD))
                .thenReturn(new Object[]{true, 1L});
        Mockito.when(ctx.pipeline())
                .thenReturn(channelPipeline);
        Mockito.when(channelPipeline.get(MainHandler.class))
                .thenReturn(handler);

        Assertions.assertDoesNotThrow(() -> authHandlerService.authHandle(ctx, AuthAndRegRequest.AUTH, actualLogin, actualPassword));

        if (result.equals("OK_AUTH")) {
            Mockito.verify(handler).setUserService(userServiceCaptor.capture());
            assertEquals(userService, userServiceCaptor.getValue());
        }
        Mockito.verify(ctx).writeAndFlush(new AuthResult(AuthAndRegResult.valueOf(result)));
    }

    @ParameterizedTest
    @MethodSource("authHandleRegistrationTestProvider")
    public void authHandle_Registration(String actualLogin, String actualPassword, AuthResult result) {
PropertiesLoader.getPASSWORD();
        Mockito.when(userService.createUser(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(false);
        Mockito.when(userService.createUser(Mockito.eq(WRIGHT_LOGIN),
                Mockito.eq(WRIGHT_PASSWORD),
                Mockito.endsWith(File.separator + actualLogin)))
                .thenReturn(true);

        Assertions.assertDoesNotThrow(() -> authHandlerService.authHandle(ctx, AuthAndRegRequest.REGISTRATION, actualLogin, actualPassword));

        Mockito.verify(ctx).writeAndFlush(authResult.capture());
        Assertions.assertEquals(result, authResult.getValue());

        Assertions.assertDoesNotThrow(() -> removeTestUserDirectory(actualLogin));
    }

    private static Stream<Arguments> authHandleRegistrationTestProvider() {
        return Stream.of(
                Arguments.arguments(WRIGHT_LOGIN, WRIGHT_PASSWORD, new AuthResult(AuthAndRegResult.OK_REG, WRIGHT_LOGIN)),
                Arguments.arguments(WRIGHT_LOGIN, "123", new AuthResult(AuthAndRegResult.FAIL_REG, WRIGHT_LOGIN))
        );
    }

    private void removeTestUserDirectory(String login) throws IOException {
        Path path = Paths.get(PropertiesLoader.getROOT_DIRECTORY() + File.separator + login);
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }
}