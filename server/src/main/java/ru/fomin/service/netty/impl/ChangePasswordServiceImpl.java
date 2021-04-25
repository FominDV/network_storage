package ru.fomin.service.netty.impl;

import io.netty.channel.ChannelHandlerContext;
import ru.fomin.dto.requests.ChangePasswordRequest;
import ru.fomin.dto.responses.ChangePasswordResponse;
import ru.fomin.service.db.UserService;
import ru.fomin.service.netty.ChangePasswordService;

/**
 * Service for changing password of user.
 */
public class ChangePasswordServiceImpl implements ChangePasswordService {

    @Override
    public void changePassword(ChannelHandlerContext ctx, ChangePasswordRequest request, UserService userService, Long userId) {
        boolean isSuccessful = false;
        if (userService.changePassword(request.getCurrentPassword(), request.getPassword(), userId)) {
            isSuccessful = true;
        }
        ctx.writeAndFlush(new ChangePasswordResponse(isSuccessful));
    }

}
