package ru.fomin.service.netty;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import ru.fomin.server.handler.MainHandler;
import ru.fomin.util.PropertiesLoader;
import ru.fomin.dto.requests.FileManipulationRequest;
import ru.fomin.dto.responses.CurrentDirectoryEntityList;
import ru.fomin.entity.Directory;
import ru.fomin.entity.FileData;
import ru.fomin.enumeration.FileManipulateResponse;
import ru.fomin.enumeration.Prefix;
import ru.fomin.rervice.FileTransmitterService;
import ru.fomin.service.db.DirectoryService;
import ru.fomin.service.db.FileDataService;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for process FileManipulationRequest message from client.
 */
@Log4j2
public class FileManipulationService {

    //services
    private final DirectoryService DIRECTORY_SERVICE;
    private final FileDataService FILE_DATA_SERVICE;

    private FileTransmitterService fileTransmitterService;
    private Thread fileTransmitterThread;

    public FileManipulationService(DirectoryService DIRECTORY_SERVICE, FileDataService FILE_DATA_SERVICE) {
        this.DIRECTORY_SERVICE = DIRECTORY_SERVICE;
        this.FILE_DATA_SERVICE = FILE_DATA_SERVICE;
    }

    /**
     * Verifies type of request and delegate it to needed method.
     */
    public void requestFileHandle(ChannelHandlerContext ctx, FileManipulationRequest request, Directory currentDirectory) throws IOException {
        switch (request.getRequest()) {
            //returns all files and nested directories from current directory
            case GET_FILES_LIST:
                sendFileList(ctx, currentDirectory);
                break;
            case DELETE_FILE:
                deleteFile(ctx, request.getId(), currentDirectory);
                break;
            case DELETE_DIR:
                deleteDirectory(ctx, request.getId());
                break;
            //transfer file from server to client
            case DOWNLOAD:
                upload(ctx, request.getId());
                break;
            case OUT_DIR:
                sendFileListOfParentDirectory(ctx, request.getId());
                break;
            case INTO_DIR:
                sendFileList(ctx, request.getId());
                break;
            default:
                log.error(String.format("Unknown response \"%s\" from server", request.getRequest()));
        }
    }

    private void sendFileListOfParentDirectory(ChannelHandlerContext ctx, Long currentDirectoryId) {
        Directory newCurrentDirectory = DIRECTORY_SERVICE.getDirectoryById(currentDirectoryId).getParentDirectory();
        ctx.pipeline().get(MainHandler.class).setCurrentDirectory(newCurrentDirectory);
        sendFileList(ctx, newCurrentDirectory);
    }

    private void deleteFile(ChannelHandlerContext ctx, Long id, Directory currentDirectory) throws IOException {
        //deletes from DB
        String fileName = FILE_DATA_SERVICE.deleteFile(id);
        //deletes real file
        Path path = Paths.get(currentDirectory.getPath(), fileName);
        Files.delete(path);
        ctx.writeAndFlush(new ru.fomin.dto.responses.FileManipulationResponse(FileManipulateResponse.FILE_REMOVED, fileName, id));
    }

    private void upload(ChannelHandlerContext ctx, Long fileId) {
        if (fileTransmitterService == null) {
            fileTransmitterService = new FileTransmitterService(dataPackage -> ctx.writeAndFlush(dataPackage));
            fileTransmitterThread = new Thread(fileTransmitterService);
            fileTransmitterThread.setDaemon(true);
            fileTransmitterThread.start();
        }
        fileTransmitterService.addFile(FILE_DATA_SERVICE.getFileById(fileId), fileId);
    }

    private void sendFileList(ChannelHandlerContext ctx, Long directoryId) {
        Directory newCurrentDirectory = DIRECTORY_SERVICE.getDirectoryById(directoryId);
        ctx.pipeline().get(MainHandler.class).setCurrentDirectory(newCurrentDirectory);
        sendFileList(ctx, newCurrentDirectory);
    }

    private void sendFileList(ChannelHandlerContext ctx, Directory currentDirectory) {
        Map<String, Long> fileMap = new HashMap<>();
        Map<String, Long> directoryMap = new HashMap<>();
        Long id = currentDirectory.getId();
        List<FileData> currentFileList = DIRECTORY_SERVICE.getFiles(id);
        List<Directory> currentDirectoryList = DIRECTORY_SERVICE.getNestedDirectories(id);
        currentFileList.forEach(fileData -> fileMap.put(Prefix.FILE_NAME_PREFIX + fileData.getName(), fileData.getId()));
        currentDirectoryList.forEach(directory -> directoryMap.put(Prefix.DIRECTORY_NAME_PREFIX + directory.getPath().substring(currentDirectory.getPath().length() + 1), directory.getId()));
        String currentDirectoryName = currentDirectory.getPath().substring(PropertiesLoader.getROOT_DIRECTORY().length());
        ctx.writeAndFlush(new CurrentDirectoryEntityList(fileMap, directoryMap, currentDirectoryName, currentDirectory.getId()));
    }

    /**
     * Deletes directory and all that it contains.
     */
    private void deleteDirectory(ChannelHandlerContext ctx, Long id) throws IOException {
        //deletes from DB
        String directoryPathString = DIRECTORY_SERVICE.deleteDirectory(id);
        //deletes real file
        Path directoryPath = Paths.get(directoryPathString);
        Files.walkFileTree(directoryPath, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
        ctx.writeAndFlush(
                new ru.fomin.dto.responses.FileManipulationResponse(FileManipulateResponse.DIRECTORY_REMOVED,
                        directoryPathString.substring(PropertiesLoader.getROOT_DIRECTORY().length()),
                        id)
        );
    }
}
