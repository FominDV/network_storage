package ru.fomin.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import ru.fomin.dto.requests.AuthRequest;
import ru.fomin.service.netty.AuthService;
import ru.fomin.service.netty.impl.AuthServiceImpl;

import java.io.IOException;

/**
 * Verifies authorizing, makes authenticate and creates new client data.
 */
public class AuthHandler extends ChannelInboundHandlerAdapter {

    private final AuthService authService;

    public AuthHandler() {
        authService = new AuthServiceImpl();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
        //if client is already authorized this request will be delegated to next handler
        if (authService.isAuthorized()) {
            ctx.fireChannelRead(msg);
            return;
        }

        try {
            if (msg instanceof AuthRequest) {
                AuthRequest request = (AuthRequest) msg;
                authService.authHandle(ctx, request.getAuthAndRegRequest(), request.getLogin(), request.getPassword());
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