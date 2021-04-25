package ru.fomin.service.netty;

import io.netty.channel.ChannelHandlerContext;
import ru.fomin.dto.requests.FileManipulationRequest;
import ru.fomin.entity.Directory;

import java.io.IOException;

/**
 * Service for process FileManipulationRequest message from client.
 */
public interface FileManipulationService {

    /**
     * Verifies type of request and delegate it to needed method.
     */
    void requestFileHandle(ChannelHandlerContext ctx, FileManipulationRequest request, Directory currentDirectory) throws IOException;

}
