package ru.fomin.services.handler_services;

import io.netty.channel.ChannelHandlerContext;
import ru.fomin.dto.requests.AuthRequest;
import ru.fomin.dto.responses.AuthResult;
import ru.fomin.core.MainHandler;
import ru.fomin.services.db_services.UserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Service for process AuthRequest message from client.
 */
public class AuthHandlerService {

    //Services
    private final UserService userService;

    //For verifying authorization of client
    private boolean isAuthorized;

    public AuthHandlerService() {
        userService = new UserService();
        isAuthorized = false;
    }

    /**
     * Processes request from client when he is not authorized yet.
     */
    public void authHandle(ChannelHandlerContext ctx, AuthRequest.RequestType type, String login, String password) throws IOException {
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
                    ctx.writeAndFlush(new AuthResult(AuthResult.Result.OK_AUTH));
                } else {
                    ctx.writeAndFlush(new AuthResult(AuthResult.Result.FAIL_AUTH));
                }
                break;
            //If client want to create new account
            case REGISTRATION:
                String root = MainHandler.getMainPath() + File.separator + login;
                //Verifies existing duplicate login
                if (userService.createUser(login, password, root)) {
                    Files.createDirectory(Paths.get(root));
                    ctx.writeAndFlush(new AuthResult(AuthResult.Result.OK_REG, login));
                } else {
                    ctx.writeAndFlush(new AuthResult(AuthResult.Result.FAIL_REG, login));
                }
                break;
            default:
                System.out.println(String.format("Unknown request \"%s\"", type));
        }
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }
}
