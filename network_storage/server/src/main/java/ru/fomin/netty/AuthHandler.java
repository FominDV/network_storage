package ru.fomin.netty;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import ru.fomin.AuthCommand;
import ru.fomin.AuthResult;
import ru.fomin.entities.User;
import ru.fomin.services.UserService;

import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.exists;
import static ru.fomin.netty.Server.STORAGE_DIR;


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
            if (msg instanceof AuthCommand) {
                AuthCommand com = (AuthCommand) msg;

                autorized = USER_SERVICE.isValidUserData(com.login, com.password);

                if (autorized) {
                    Path dir = Paths.get(STORAGE_DIR, com.login);
                    if (!exists(dir)) {

                        createDirectory(Paths.get(STORAGE_DIR));
                        createDirectory(dir);
                    }
                    MainHandler handler = ctx.pipeline().get(MainHandler.class);
                    handler.setUserDir(dir);

                    ctx.writeAndFlush(AuthResult.ok());
                } else {
                    ctx.writeAndFlush(AuthResult.fail());
                }
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

}