package ru.fomin.services.handler_services;

import io.netty.channel.ChannelHandlerContext;
import ru.fomin.classes.Constants;
import ru.fomin.commands.CurrentDirectoryEntityList;
import ru.fomin.commands.FileManipulationRequest;
import ru.fomin.commands.FileManipulationResponse;
import ru.fomin.classes.FileTransmitter;
import ru.fomin.core.MainHandler;
import ru.fomin.entities.Directory;
import ru.fomin.entities.FileData;
import ru.fomin.services.db_services.DirectoryService;
import ru.fomin.services.db_services.FileDataService;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**Service for process FileManipulationRequest message from client.*/
public class MainHandlerFileManipulationService {

    private final DirectoryService DIRECTORY_SERVICE;
    private final FileDataService FILE_DATA_SERVICE;
    private FileTransmitter fileTransmitter;
    private Thread fileTransmitterThread;

    public MainHandlerFileManipulationService(DirectoryService DIRECTORY_SERVICE, FileDataService FILE_DATA_SERVICE) {
        this.DIRECTORY_SERVICE = DIRECTORY_SERVICE;
        this.FILE_DATA_SERVICE = FILE_DATA_SERVICE;
    }


    public void requestFileHandle(ChannelHandlerContext ctx, FileManipulationRequest request, Directory currentDirectory) throws IOException {
        switch (request.getRequest()) {
            case GET_FILES_LIST:
                sendFileList(ctx, currentDirectory);
                break;
            case DELETE_FILE:
                deleteFile(ctx, request.getId(), currentDirectory);
                break;
            case DELETE_DIR:
                deleteDirectory(ctx, request.getId());
                break;
            case DOWNLOAD:
                upload(ctx, request.getId());
                break;
            default:
                System.out.println(String.format("Unknown response \"%s\" from server", request.getRequest()));
        }
    }

    private void deleteFile(ChannelHandlerContext ctx, Long id, Directory currentDirectory) throws IOException {
        String fileName = FILE_DATA_SERVICE.deleteFile(id);
        Path path = Paths.get(currentDirectory.getPath(), fileName);
        Files.delete(path);
        ctx.writeAndFlush(new FileManipulationResponse(FileManipulationResponse.Response.FILE_REMOVED, fileName, id));
    }

    private void upload(ChannelHandlerContext ctx, Long fileId) {
        if (fileTransmitter == null) {
            fileTransmitter = new FileTransmitter(dataPackage->ctx.writeAndFlush(dataPackage));
            fileTransmitterThread = new Thread(fileTransmitter);
            fileTransmitterThread.setDaemon(true);
            fileTransmitterThread.start();
        }
        fileTransmitter.addFile(FILE_DATA_SERVICE.getFileById(fileId), fileId);
    }

    private void sendFileList(ChannelHandlerContext ctx, Directory currentDirectory) throws IOException {
        Map<String, Long> fileMap = new HashMap<>();
        Map<String, Long> directoryMap = new HashMap<>();
        Long id = currentDirectory.getId();
        List<FileData> currentFileList = DIRECTORY_SERVICE.getFiles(id);
        List<Directory> currentDirectoryList = DIRECTORY_SERVICE.getNestedDirectories(id);
        currentFileList.forEach(fileData -> fileMap.put(Constants.getFileNamePrefix() + fileData.getName(), fileData.getId()));
        currentDirectoryList.forEach(directory -> directoryMap.put(Constants.getDirectoryNamePrefix() + directory.getPath().substring(currentDirectory.getPath().length() + 1), directory.getId()));
        String currentDirectoryName = currentDirectory.getPath().substring(MainHandler.getMainPath().length());
        ctx.writeAndFlush(new CurrentDirectoryEntityList(fileMap, directoryMap, currentDirectoryName, currentDirectory.getId()));
    }

    private void deleteDirectory(ChannelHandlerContext ctx, Long id) throws IOException {
        String directoryPathString = DIRECTORY_SERVICE.deleteDirectory(id);
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
        ctx.writeAndFlush(new FileManipulationResponse(FileManipulationResponse.Response.DIRECTORY_REMOVED, directoryPathString.substring(MainHandler.getMainPath().length()), id));
    }
}
