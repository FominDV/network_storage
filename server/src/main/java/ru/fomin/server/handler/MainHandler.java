package ru.fomin.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.Setter;
import ru.fomin.dto.requests.ChangePasswordRequest;
import ru.fomin.dto.requests.CreatingAndUpdatingManipulationRequest;
import ru.fomin.dto.requests.FileManipulationRequest;
import ru.fomin.entities.Directory;
import ru.fomin.dto.file_packages.FileChunkPackage;
import ru.fomin.dto.file_packages.FileDataPackage;
import ru.fomin.service.db.DirectoryService;
import ru.fomin.service.db.FileDataService;
import ru.fomin.service.db.UserService;
import ru.fomin.service.handler.MainHandlerChangePasswordService;
import ru.fomin.service.handler.MainHandlerDownloadService;
import ru.fomin.service.handler.MainHandlerFileManipulationService;
import ru.fomin.service.handler.MainHandlerRequestDirectoryService;

import java.io.IOException;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Processes requests from client after successful authorization.
 */
@Setter
public class MainHandler extends ChannelInboundHandlerAdapter {

    //services
    private final DirectoryService directoryService;
    private final FileDataService fileDataService;
    private final MainHandlerFileManipulationService mainHandlerFileManipulationService;
    private final MainHandlerRequestDirectoryService mainHandlerRequestDirectoryService;
    private final MainHandlerDownloadService mainHandlerDownloadService;
    private final MainHandlerChangePasswordService mainHandlerChangePasswordService;

    private UserService userService;
    private Directory currentDirectory;
    private Long userId;

    public MainHandler() {
        directoryService = new DirectoryService();
        fileDataService = new FileDataService();
        mainHandlerFileManipulationService = new MainHandlerFileManipulationService(directoryService, fileDataService);
        mainHandlerRequestDirectoryService = new MainHandlerRequestDirectoryService(directoryService, fileDataService);
        mainHandlerDownloadService = new MainHandlerDownloadService(directoryService, fileDataService);
        mainHandlerChangePasswordService = new MainHandlerChangePasswordService();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
        try {
            if (msg instanceof FileManipulationRequest) {
                mainHandlerFileManipulationService.requestFileHandle(ctx, (FileManipulationRequest) msg, currentDirectory);
            } else if (msg instanceof FileDataPackage) {
                mainHandlerDownloadService.downloadSmallFile(ctx, (FileDataPackage) msg);
            } else if (msg instanceof FileChunkPackage) {
                mainHandlerDownloadService.downloadBigFile(ctx, (FileChunkPackage) msg);
            } else if (msg instanceof CreatingAndUpdatingManipulationRequest) {
                mainHandlerRequestDirectoryService.requestDirectoryHandle(ctx, (CreatingAndUpdatingManipulationRequest) msg);
            } else if (msg instanceof ChangePasswordRequest) {
                mainHandlerChangePasswordService.changePassword(ctx, (ChangePasswordRequest) msg, userService, userId);
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