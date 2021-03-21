package ru.fomin.services.handler_services;

import io.netty.channel.ChannelHandlerContext;
import ru.fomin.classes.FileChunkDownloader;
import ru.fomin.commands.FileManipulationResponse;
import ru.fomin.file_packages.FileChunkPackage;
import ru.fomin.file_packages.FileDataPackage;
import ru.fomin.services.db_services.DirectoryService;
import ru.fomin.services.db_services.FileDataService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainHandlerDownloadService {

    private final DirectoryService DIRECTORY_SERVICE;
    private final FileDataService FILE_DATA_SERVICE;
    private final FileChunkDownloader fileChunkDownloader;

    public MainHandlerDownloadService(DirectoryService DIRECTORY_SERVICE, FileDataService FILE_DATA_SERVICE) {
        this.DIRECTORY_SERVICE = DIRECTORY_SERVICE;
        this.FILE_DATA_SERVICE = FILE_DATA_SERVICE;
        fileChunkDownloader = new FileChunkDownloader();
    }

    public void downloadSmallFile(ChannelHandlerContext ctx, FileDataPackage pack) throws IOException {
        Long directoryId = pack.getDirectoryId();
        String fileName = pack.getFilename();
        if (isFileExist(ctx, fileName, directoryId)) {
            return;
        }
        Path path = Paths.get(DIRECTORY_SERVICE.getDirectoryPathById(directoryId) + File.separator + fileName);
        Files.write(path, pack.getData());
        Long id = FILE_DATA_SERVICE.createFile(fileName, DIRECTORY_SERVICE.getDirectoryById(directoryId));
        ctx.writeAndFlush(new FileManipulationResponse(FileManipulationResponse.Response.FILE_UPLOADED, fileName, id));
    }

    public void downloadBigFile(ChannelHandlerContext ctx, FileChunkPackage pack) throws IOException {
        String fileName = pack.getFilename();
        Long directoryId = pack.getDirectoryId();
        if (isFileExist(ctx, fileName, directoryId)) {
            return;
        }
        Runnable action = () -> {
            Long id = FILE_DATA_SERVICE.createFile(fileName, DIRECTORY_SERVICE.getDirectoryById(directoryId));
            ctx.writeAndFlush(new FileManipulationResponse(FileManipulationResponse.Response.FILE_UPLOADED, fileName, id));
        };
        fileChunkDownloader.writeFileChunk(pack, action, DIRECTORY_SERVICE.getDirectoryPathById(directoryId));
    }

    //Verify existing of file and send error message
    private boolean isFileExist(ChannelHandlerContext ctx, String fileName, Long directory) {
        if (DIRECTORY_SERVICE.isFileExist(fileName, DIRECTORY_SERVICE.getDirectoryById(directory))) {
            ctx.writeAndFlush(new FileManipulationResponse(FileManipulationResponse.Response.FILE_ALREADY_EXIST, fileName));
            return true;
        } else {
            return false;
        }
    }
}
