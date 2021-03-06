package ru.fomin.service.netty.impl;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import ru.fomin.dto.requests.FileManipulationRequest;
import ru.fomin.dto.responses.CurrentDirectoryEntityList;
import ru.fomin.entity.Directory;
import ru.fomin.entity.FileData;
import ru.fomin.enumeration.FileManipulateResponse;
import ru.fomin.enumeration.Prefix;
import ru.fomin.service.FileTransmittable;
import ru.fomin.service.impl.FileTransmitterService;
import ru.fomin.server.handler.MainHandler;
import ru.fomin.service.db.DirectoryService;
import ru.fomin.service.db.FileDataService;
import ru.fomin.service.netty.FileManipulationService;
import ru.fomin.util.PropertiesLoader;

import java.io.File;
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
public class FileManipulationServiceImpl implements FileManipulationService {

    //services
    private final DirectoryService directoryService;
    private final FileDataService fileDataService;

    private FileTransmittable fileTransmittable;
    private Thread fileTransmitterThread;

    public FileManipulationServiceImpl(DirectoryService directoryService, FileDataService fileDataService) {
        this.directoryService = directoryService;
        this.fileDataService = fileDataService;
    }

    /**
     * Verifies type of request and delegate it to needed method.
     */
    @Override
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
        Directory newCurrentDirectory = directoryService.getDirectoryById(currentDirectoryId).getParentDirectory();
        ctx.pipeline().get(MainHandler.class).setCurrentDirectory(newCurrentDirectory);
        sendFileList(ctx, newCurrentDirectory);
    }

    private void deleteFile(ChannelHandlerContext ctx, Long id, Directory currentDirectory) throws IOException {
        //deletes from DB
        String fileName = fileDataService.deleteFile(id);
        //deletes real file
        Path path = Paths.get(currentDirectory.getPath(), fileName);
        Files.delete(path);
        ctx.writeAndFlush(new ru.fomin.dto.responses.FileManipulationResponse(FileManipulateResponse.FILE_REMOVED, fileName, id));
    }

    private void upload(ChannelHandlerContext ctx, Long fileId) {
        if (fileTransmittable == null) {
            fileTransmittable = new FileTransmitterService(dataPackage -> ctx.writeAndFlush(dataPackage));
            fileTransmitterThread = new Thread(fileTransmittable);
            fileTransmitterThread.setDaemon(true);
            fileTransmitterThread.start();
        }
        fileTransmittable.addFile(fileDataService.getFileById(fileId), fileId);
    }

    private void sendFileList(ChannelHandlerContext ctx, Long directoryId) {
        Directory newCurrentDirectory = directoryService.getDirectoryById(directoryId);
        ctx.pipeline().get(MainHandler.class).setCurrentDirectory(newCurrentDirectory);
        sendFileList(ctx, newCurrentDirectory);
    }

    private void sendFileList(ChannelHandlerContext ctx, Directory currentDirectory) {
        Map<String, Long> fileMap = new HashMap<>();
        Map<String, Long> directoryMap = new HashMap<>();
        Map<String, Long> contentSizeMap = new HashMap<>();

        Long id = currentDirectory.getId();
        List<FileData> currentFileList = directoryService.getFiles(id);
        List<Directory> currentDirectoryList = directoryService.getNestedDirectories(id);
        currentFileList.forEach(fileData -> {
            String filename = Prefix.FILE_NAME_PREFIX + fileData.getName();
            fileMap.put(filename, fileData.getId());
            contentSizeMap.put(filename, getFileSize(fileData));
        });
        currentDirectoryList.forEach(directory -> {
            String directoryName = Prefix.DIRECTORY_NAME_PREFIX + directory.getPath().substring(currentDirectory.getPath().length() + 1);
            directoryMap.put(directoryName, directory.getId());
            contentSizeMap.put(directoryName, getDirectorySize(directory));
        });
        String currentDirectoryName = currentDirectory.getPath().substring(PropertiesLoader.getROOT_DIRECTORY().length());
        ctx.writeAndFlush(new CurrentDirectoryEntityList(fileMap,
                directoryMap,
                contentSizeMap,
                currentDirectoryName,
                currentDirectory.getId()));
    }

    /**
     * Deletes directory and all that it contains.
     */
    private void deleteDirectory(ChannelHandlerContext ctx, Long id) throws IOException {
        //deletes from DB
        String directoryPathString = directoryService.deleteDirectory(id);
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

    @SneakyThrows
    /**Returns the file size in bytes.*/
    private Long getFileSize(FileData fileData) {
        Path path = Paths.get(fileData.getDirectory().getPath() + File.separator + fileData.getName());
        return Files.size(path);
    }

    @SneakyThrows
    /**Returns the directory and all its entry size in bytes.*/
    private Long getDirectorySize(Directory directory) {
        Path path = Path.of(directory.getPath());
        DirectorySizeCounterFileVisitor fileVisitor = new DirectorySizeCounterFileVisitor();
        Files.walkFileTree(path, fileVisitor);
        return fileVisitor.getDirectorySize();
    }

    /**
     * Class for calculating the size of directory.
     */
    class DirectorySizeCounterFileVisitor extends SimpleFileVisitor<Path> {

        @Getter
        private Long directorySize = 0L;

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            directorySize += attrs.size();
            return FileVisitResult.CONTINUE;
        }
    }

}
