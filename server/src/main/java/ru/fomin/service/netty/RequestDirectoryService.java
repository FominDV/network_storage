package ru.fomin.service.netty;

import io.netty.channel.ChannelHandlerContext;
import ru.fomin.dto.requests.CreatingAndUpdatingManipulationRequest;

import java.io.IOException;

/**
 * Service for process CreatingAndUpdatingRequest message from client.
 */
public interface RequestDirectoryService {

    /**
     * Verifies type of request and processes it.
     */
    void requestDirectoryHandle(ChannelHandlerContext ctx, CreatingAndUpdatingManipulationRequest request) throws IOException;

}
