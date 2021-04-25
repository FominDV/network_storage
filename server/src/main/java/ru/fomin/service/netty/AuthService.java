package ru.fomin.service.netty;

import io.netty.channel.ChannelHandlerContext;
import ru.fomin.enumeration.AuthAndRegRequest;

/**
 * Service for process AuthRequest message from client.
 */
public interface AuthService {

    /**
     * Processes request from client when he is not authorized yet.
     */
    void authHandle(ChannelHandlerContext ctx, AuthAndRegRequest type, String login, String password);

    boolean isAuthorized();

}
