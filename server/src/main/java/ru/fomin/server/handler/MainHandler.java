package ru.fomin.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.Setter;
import ru.fomin.dto.requests.ChangePasswordRequest;
import ru.fomin.dto.requests.CreatingAndUpdatingManipulationRequest;
import ru.fomin.dto.requests.FileManipulationRequest;
import ru.fomin.entity.Directory;
import ru.fomin.dto.file_packages.FileChunkPackage;
import ru.fomin.dto.file_packages.FileDataPackage;
import ru.fomin.service.db.DirectoryService;
import ru.fomin.service.db.FileDataService;
import ru.fomin.service.db.UserService;
import ru.fomin.service.netty.ChangePasswordService;
import ru.fomin.service.netty.DownloadService;
import ru.fomin.service.netty.FileManipulationService;
import ru.fomin.service.netty.RequestDirectoryService;

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
    private final FileManipulationService fileManipulationService;
    private final RequestDirectoryService requestDirectoryService;
    private final DownloadService downloadService;
    private final ChangePasswordService changePasswordService;

    private UserService userService;
    private Directory currentDirectory;
    private Long userId;

    public MainHandler() {
        directoryService = new DirectoryService();
        fileDataService = new FileDataService();
        fileManipulationService = new FileManipulationService(directoryService, fileDataService);
        requestDirectoryService = new RequestDirectoryService(directoryService, fileDataService);
        downloadService = new DownloadService(directoryService, fileDataService);
        changePasswordService = new ChangePasswordService();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
        try {
            if (msg instanceof FileManipulationRequest) {
                fileManipulationService.requestFileHandle(ctx, (FileManipulationRequest) msg, currentDirectory);
            } else if (msg instanceof FileDataPackage) {
                downloadService.downloadSmallFile(ctx, (FileDataPackage) msg);
            } else if (msg instanceof FileChunkPackage) {
                downloadService.downloadBigFile(ctx, (FileChunkPackage) msg);
            } else if (msg instanceof CreatingAndUpdatingManipulationRequest) {
                requestDirectoryService.requestDirectoryHandle(ctx, (CreatingAndUpdatingManipulationRequest) msg);
            } else if (msg instanceof ChangePasswordRequest) {
                changePasswordService.changePassword(ctx, (ChangePasswordRequest) msg, userService, userId);
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