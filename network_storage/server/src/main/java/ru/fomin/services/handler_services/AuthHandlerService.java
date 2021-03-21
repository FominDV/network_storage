package ru.fomin.services.handler_services;

import io.netty.channel.ChannelHandlerContext;
import ru.fomin.commands.AuthRequest;
import ru.fomin.commands.AuthResult;
import ru.fomin.core.MainHandler;
import ru.fomin.services.db_services.UserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AuthHandlerService {

    private final UserService USER_SERVICE;
    private boolean authorized;

    public AuthHandlerService() {
        USER_SERVICE = new UserService();
        authorized = false;
    }

    public void authHandle(ChannelHandlerContext ctx, AuthRequest.RequestType type, String login, String password) throws IOException {
        switch (type) {
            case AUTH:
                authorized = USER_SERVICE.isValidUserData(login, password);
                if (authorized) {
                    MainHandler handler = ctx.pipeline().get(MainHandler.class);
                    handler.setUserDir(USER_SERVICE.getRootDirectoryByLogin(login));
                    ctx.writeAndFlush(new AuthResult(AuthResult.Result.OK_AUTH));
                } else {
                    ctx.writeAndFlush(new AuthResult(AuthResult.Result.FAIL_AUTH));
                }
                break;
            case REGISTRATION:
                String root = MainHandler.getMainPath() + File.separator + login;
                if (USER_SERVICE.createUser(login, password, root)) {
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
        return authorized;
    }
}
