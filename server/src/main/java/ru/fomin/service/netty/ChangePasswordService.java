package ru.fomin.service.netty;

import io.netty.channel.ChannelHandlerContext;
import ru.fomin.dto.requests.ChangePasswordRequest;
import ru.fomin.service.db.UserService;
import ru.fomin.service.db.impl.UserServiceImpl;

/**
 * Service for changing password of user.
 */
public interface ChangePasswordService {

    void changePassword(ChannelHandlerContext ctx, ChangePasswordRequest request, UserService userService, Long userId);

}
