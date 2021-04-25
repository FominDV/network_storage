package ru.fomin.service.handler;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import ru.fomin.dto.requests.ChangePasswordRequest;
import ru.fomin.dto.responses.ChangePasswordResponse;
import ru.fomin.service.db.UserService;

/**
 * Service for changing password of user.
 */
@RequiredArgsConstructor
public class MainHandlerChangePasswordService {

    public void changePassword(ChannelHandlerContext ctx, ChangePasswordRequest request, UserService userService, Long userId) {
        boolean isSuccessful = false;
        if (userService.changePassword(request.getCurrentPassword(), request.getPassword(), userId)) {
            isSuccessful = true;
        }
        ctx.writeAndFlush(new ChangePasswordResponse(isSuccessful));
    }

}
