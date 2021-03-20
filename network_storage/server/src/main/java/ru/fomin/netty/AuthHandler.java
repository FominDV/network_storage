package ru.fomin.netty;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import ru.fomin.need.AuthCommand;
import ru.fomin.need.AuthResult;
import ru.fomin.services.UserService;


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
                    MainHandler handler = ctx.pipeline().get(MainHandler.class);
                    handler.setUserDir(USER_SERVICE.getRootDirectoryByLogin(com.login));
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