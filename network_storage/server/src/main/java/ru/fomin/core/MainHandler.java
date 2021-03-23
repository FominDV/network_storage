package ru.fomin.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import ru.fomin.commands.CreatingAndUpdatingManipulationRequest;
import ru.fomin.commands.FileManipulationRequest;
import ru.fomin.entities.Directory;
import ru.fomin.file_packages.FileChunkPackage;
import ru.fomin.file_packages.FileDataPackage;
import ru.fomin.services.db_services.DirectoryService;
import ru.fomin.services.db_services.FileDataService;
import ru.fomin.services.handler_services.MainHandlerDownloadService;
import ru.fomin.services.handler_services.MainHandlerFileManipulationService;
import ru.fomin.services.handler_services.MainHandlerRequestDirectoryService;

import java.io.IOException;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Processes requests from client after successful authorization.
 */
public class MainHandler extends ChannelInboundHandlerAdapter {

    //services
    private final DirectoryService directoryService;
    private final FileDataService fileDataService;
    private final MainHandlerFileManipulationService mainHandlerFileManipulationService;
    private final MainHandlerRequestDirectoryService mainHandlerRequestDirectoryService;
    private final MainHandlerDownloadService mainHandlerDownloadService;

    private static final String MAIN_PATH = "main_repository";
    private Directory currentDirectory;

    public MainHandler() {
        directoryService = new DirectoryService();
        fileDataService = new FileDataService();
        mainHandlerFileManipulationService = new MainHandlerFileManipulationService(directoryService, fileDataService);
        mainHandlerRequestDirectoryService = new MainHandlerRequestDirectoryService(directoryService, fileDataService);
        mainHandlerDownloadService = new MainHandlerDownloadService(directoryService, fileDataService);
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

    public static String getMainPath() {
        return MAIN_PATH;
    }

    public void setUserDir(Directory directory) {
        currentDirectory = directory;
    }
}