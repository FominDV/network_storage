package ru.fomin.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import ru.fomin.commands.CreatingAndUpdatingManipulationCommand;
import ru.fomin.commands.CurrentDirectoryEntityList;
import ru.fomin.commands.FileManipulationRequest;
import ru.fomin.commands.FileManipulationResponse;
import ru.fomin.entities.Directory;
import ru.fomin.entities.FileData;
import ru.fomin.classes.Constants;
import ru.fomin.classes.FileChunkDownloader;
import ru.fomin.file_packages.FileChunkPackage;
import ru.fomin.file_packages.FileDataPackage;
import ru.fomin.services.db_services.DirectoryService;
import ru.fomin.services.db_services.FileDataService;
import ru.fomin.services.db_services.UserService;
import ru.fomin.services.handler_services.MainHandlerDownloadService;
import ru.fomin.services.handler_services.MainHandlerFileManipulationService;
import ru.fomin.services.handler_services.MainHandlerRequestDirectoryService;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

public class MainHandler extends ChannelInboundHandlerAdapter {

    //services
    private final DirectoryService DIRECTORY_SERVICE;
    private final FileDataService FILE_DATA_SERVICE;
    private final MainHandlerFileManipulationService mainHandlerFileManipulationService;
    private final MainHandlerRequestDirectoryService mainHandlerRequestDirectoryService;
    private final MainHandlerDownloadService mainHandlerDownloadService;

    private static final String MAIN_PATH = "main_repository";
    private Directory currentDirectory;

    public MainHandler() {
        DIRECTORY_SERVICE = new DirectoryService();
        FILE_DATA_SERVICE = new FileDataService();
        mainHandlerFileManipulationService = new MainHandlerFileManipulationService(DIRECTORY_SERVICE, FILE_DATA_SERVICE);
        mainHandlerRequestDirectoryService = new MainHandlerRequestDirectoryService(DIRECTORY_SERVICE, FILE_DATA_SERVICE);
        mainHandlerDownloadService = new MainHandlerDownloadService(DIRECTORY_SERVICE, FILE_DATA_SERVICE);
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
            } else if (msg instanceof CreatingAndUpdatingManipulationCommand) {
                mainHandlerRequestDirectoryService.requestDirectoryHandle(ctx, (CreatingAndUpdatingManipulationCommand) msg, currentDirectory);
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