package ru.fomin.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import ru.fomin.commands.AuthRequest;
import ru.fomin.services.handler_services.AuthHandlerService;

import java.io.IOException;

public class AuthHandler extends ChannelInboundHandlerAdapter {


    private final AuthHandlerService authHandlerService;

    public AuthHandler() {
        authHandlerService = new AuthHandlerService();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
        if (authHandlerService.isAuthorized()) {
            ctx.fireChannelRead(msg);
            return;
        }

        try {
            if (msg instanceof AuthRequest) {
                AuthRequest request = (AuthRequest) msg;
                authHandlerService.authHandle(ctx, request.getRequestType(), request.getLogin(), request.getPassword());
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