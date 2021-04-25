package ru.fomin.service.handler;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import ru.fomin.core.PropertiesLoader;
import ru.fomin.dto.responses.AuthResult;
import ru.fomin.core.MainHandler;
import ru.fomin.enumeration.AuthAndRegRequest;
import ru.fomin.enumeration.AuthAndRegResult;
import ru.fomin.service.db.UserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Service for process AuthRequest message from client.
 */
@Log4j2
public class AuthHandlerService {

    //Services
    private final UserService userService;

    //For verifying authorization of client
    @Getter
    private boolean isAuthorized;

    public AuthHandlerService() {
        userService = new UserService();
        isAuthorized = false;
    }

    public AuthHandlerService(UserService userService) {
        this.userService = userService;
        isAuthorized = false;
    }

    /**
     * Processes request from client when he is not authorized yet.
     */
    public void authHandle(ChannelHandlerContext ctx, AuthAndRegRequest type, String login, String password) {
        switch (type) {
            //If client want to authorize
            case AUTH:
                Object[] responseFromDB = userService.isValidUserData(login, password);
                isAuthorized = (Boolean) responseFromDB[0];
                if (isAuthorized) {
                    MainHandler handler = ctx.pipeline().get(MainHandler.class);
                    handler.setCurrentDirectory(userService.getRootDirectoryByLogin(login));
                    handler.setUserService(userService);
                    handler.setUserId((Long) responseFromDB[1]);
                    ctx.writeAndFlush(new AuthResult(AuthAndRegResult.OK_AUTH));
                } else {
                    ctx.writeAndFlush(new AuthResult(AuthAndRegResult.FAIL_AUTH));
                }
                break;
            //If client want to create new account
            case REGISTRATION:
                String root = PropertiesLoader.getROOT_DIRECTORY() + File.separator + login;
                //Verifies existing duplicate login
                if (userService.createUser(login, password, root)) {
                    createUserRootDirectory(root);
                    ctx.writeAndFlush(new AuthResult(AuthAndRegResult.OK_REG, login));
                } else {
                    ctx.writeAndFlush(new AuthResult(AuthAndRegResult.FAIL_REG, login));
                }
                break;
            default:
                log.error(String.format("Unknown request \"%s\"", type));
        }
    }

    private void createUserRootDirectory(String stringPath) {
        try {
            Files.createDirectory(Paths.get(stringPath));
        } catch (IOException e) {
            log.error(String.format("Creation of directory \"%s\" was failed: %s", stringPath, e.getCause()));
        }
    }
}
