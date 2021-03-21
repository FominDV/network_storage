package ru.fomin.core.handlers;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import ru.fomin.commands.AuthRequest;
import ru.fomin.commands.AuthResult;
import ru.fomin.services.UserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class AuthHandler
        extends ChannelInboundHandlerAdapter {

    private boolean autorized;
    private final UserService USER_SERVICE;


    public AuthHandler() {
        autorized = false;
        USER_SERVICE = new UserService();
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        if (autorized) {
            ctx.fireChannelRead(msg);
            return;
        }

        try {
            if (msg instanceof AuthRequest) {
                AuthRequest request = (AuthRequest) msg;
                authHandle(ctx, request.getRequestType(),request.getLogin(),request.getPassword());
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private void authHandle(ChannelHandlerContext ctx, AuthRequest.RequestType type, String login, String password) throws IOException {
        switch (type){
            case AUTH:
                autorized = USER_SERVICE.isValidUserData(login, password);
                if (autorized) {
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
}