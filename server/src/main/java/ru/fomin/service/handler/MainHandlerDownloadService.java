package ru.fomin.service.handler;

import io.netty.channel.ChannelHandlerContext;
import ru.fomin.enumeration.FileManipulateResponse;
import ru.fomin.rervice.FileChunkDownloaderService;
import ru.fomin.dto.file_packages.FileChunkPackage;
import ru.fomin.dto.file_packages.FileDataPackage;
import ru.fomin.service.db.DirectoryService;
import ru.fomin.service.db.FileDataService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Service for process FileDataPackage and FileChunkPackage messages from client.
 */
public class MainHandlerDownloadService {

    //services
    private final DirectoryService DIRECTORY_SERVICE;
    private final FileDataService FILE_DATA_SERVICE;

    private final FileChunkDownloaderService fileChunkDownloaderService;

    public MainHandlerDownloadService(DirectoryService DIRECTORY_SERVICE, FileDataService FILE_DATA_SERVICE) {
        this.DIRECTORY_SERVICE = DIRECTORY_SERVICE;
        this.FILE_DATA_SERVICE = FILE_DATA_SERVICE;
        fileChunkDownloaderService = new FileChunkDownloaderService();
    }

    /**
     * Creates new file by one iteration.
     */
    public void downloadSmallFile(ChannelHandlerContext ctx, FileDataPackage pack) throws IOException {
        Long directoryId = pack.getDirectoryId();
        String fileName = pack.getFilename();
        //Verifies existing of file with this name
        if (isFileExist(ctx, fileName, directoryId)) {
            return;
        }
        Path path = Paths.get(DIRECTORY_SERVICE.getDirectoryPathById(directoryId) + File.separator + fileName);
        Files.write(path, pack.getData());
        //Creates file into DB and returns id of new file
        Long id = FILE_DATA_SERVICE.createFile(fileName, DIRECTORY_SERVICE.getDirectoryById(directoryId));
        ctx.writeAndFlush(new ru.fomin.dto.responses.FileManipulationResponse(FileManipulateResponse.FILE_UPLOADED, fileName, id));
    }

    /**
     * Delegates processing chunk of file to FileChunkDownloaderService.
     */
    public void downloadBigFile(ChannelHandlerContext ctx, FileChunkPackage pack) throws IOException {
        String fileName = pack.getFilename();
        Long directoryId = pack.getId();
        //Verifies existing of file with this name
        if (isFileExist(ctx, fileName, directoryId)) {
            return;
        }

        //creates action for callback
        Runnable action = () -> {
            Long id = FILE_DATA_SERVICE.createFile(fileName, DIRECTORY_SERVICE.getDirectoryById(directoryId));
            ctx.writeAndFlush(new ru.fomin.dto.responses.FileManipulationResponse(FileManipulateResponse.FILE_UPLOADED, fileName, id));
        };
        //delegates processing to FileChunkDownloaderService
        fileChunkDownloaderService.writeFileChunk(pack, action, DIRECTORY_SERVICE.getDirectoryPathById(directoryId));
    }

    /**
     * Verifies existing of this file into BD.
     * If this file already exists this method will send message about it to client.
     *
     * @return - true if this file already exists
     */
    private boolean isFileExist(ChannelHandlerContext ctx, String fileName, Long directory) {
        if (DIRECTORY_SERVICE.isFileExist(fileName, DIRECTORY_SERVICE.getDirectoryById(directory))) {
            ctx.writeAndFlush(new ru.fomin.dto.responses.FileManipulationResponse(FileManipulateResponse.FILE_ALREADY_EXIST, fileName));
            return true;
        } else {
            return false;
        }
    }
}
