package ru.fomin.netty;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import ru.fomin.entities.Directory;
import ru.fomin.entities.FileData;
import ru.fomin.need.classes.Constants;
import ru.fomin.need.commands.*;
import ru.fomin.need.classes.FileChunkDownloader;
import ru.fomin.need.file_packages.FileChunkPackage;
import ru.fomin.need.file_packages.FileDataPackage;
import ru.fomin.services.DirectoryService;
import ru.fomin.services.FileDataService;
import ru.fomin.services.UserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.Executors.newSingleThreadExecutor;


public class MainHandler extends ChannelInboundHandlerAdapter {

    //services
    private static final UserService USER_SERVICE = new UserService();
    private static final DirectoryService DIRECTORY_SERVICE = new DirectoryService();
    private static final FileDataService FILE_DATA_SERVICE = new FileDataService();
    private FileTransmitter fileTransmitter;
    private Thread fileTransmitterThread;
    private static final String MAIN_PATH = "main_repository";
    private Directory currentDirectory;
    private FileChunkDownloader fileChunkDownloader;


    public void setUserDir(Directory directory) {
        currentDirectory = directory;
        fileChunkDownloader = new FileChunkDownloader();
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
        try {
            if (msg instanceof FileManipulationRequest) {
                requestFileHandle(ctx, (FileManipulationRequest) msg);
            } else if (msg instanceof FileDataPackage) {
                downloadSmallFile(ctx, (FileDataPackage) msg);
            } else if (msg instanceof FileChunkPackage) {
                downloadBigFile(ctx, (FileChunkPackage) msg);
            } else if (msg instanceof CreatingAndUpdatingManipulationCommand) {
                requestDirectoryHandle(ctx, (CreatingAndUpdatingManipulationCommand) msg);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (fileTransmitter != null) {
            fileTransmitter.disable();
        }
        cause.printStackTrace();
        ctx.close();
    }

    private void requestDirectoryHandle(ChannelHandlerContext ctx, CreatingAndUpdatingManipulationCommand request) throws IOException {
        String newName = request.getNewName();
        Long id = request.getId();
        switch (request.getType()) {
            case CREATE:
                Long newDirectoryId;
                String newDirectory = DIRECTORY_SERVICE.getDirectoryById(id).getPath() + File.separator + newName;
                if ((newDirectoryId = DIRECTORY_SERVICE.createDirectory(currentDirectory, newDirectory)) != -1) {
                    Files.createDirectory(Paths.get(newDirectory));
                    ctx.writeAndFlush(new FileManipulationResponse(FileManipulationResponse.Response.DIR_CREATED, newName, newDirectoryId));
                } else {
                    ctx.writeAndFlush(new FileManipulationResponse(FileManipulationResponse.Response.DIR_ALREADY_EXIST, newName));
                }
                break;
            case RENAME_DIR:
                Path currentDirectoryPath = DIRECTORY_SERVICE.getDirectoryPathById(id);
                Path newDirectoryPath = Paths.get(DIRECTORY_SERVICE.renameDirectory(id, newName));
                Files.move(currentDirectoryPath, newDirectoryPath);
                ctx.writeAndFlush(new FileManipulationResponse(FileManipulationResponse.Response.RENAME_DIR, newName, id));
                break;
            case RENAME_FILE:
                Path currentFilePath = FILE_DATA_SERVICE.getFilePathById(id);
                Path newFilePath = FILE_DATA_SERVICE.renameFileData(id, newName);
                Files.move(currentFilePath, newFilePath);
                ctx.writeAndFlush(new FileManipulationResponse(FileManipulationResponse.Response.RENAME_FILE, newName, id));
                break;
            default:
                System.out.println(String.format("Unknown response \"%s\" from server", request.getType()));
        }
    }

    private void requestFileHandle(ChannelHandlerContext ctx, FileManipulationRequest request) throws IOException {
        switch (request.getRequest()) {
            case GET_FILES_LIST:
                sendFileList(ctx);
                break;
            case DELETE_FILE:
                deleteFile(ctx, request.getId());
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
        ctx.writeAndFlush(new FileManipulationResponse(FileManipulationResponse.Response.DIRECTORY_REMOVED, directoryPathString.substring(MAIN_PATH.length()), id));
    }

    private void deleteFile(ChannelHandlerContext ctx, Long id) throws IOException {
        String fileName = FILE_DATA_SERVICE.deleteFile(id);
        Path path = Paths.get(currentDirectory.getPath(), fileName);
        Files.delete(path);
        ctx.writeAndFlush(new FileManipulationResponse(FileManipulationResponse.Response.FILE_REMOVED, fileName, id));
    }

    private void upload(ChannelHandlerContext ctx, Long fileId) {
        if (fileTransmitter == null) {
            fileTransmitter = new FileTransmitter(ctx);
            fileTransmitterThread = new Thread(fileTransmitter);
            fileTransmitterThread.setDaemon(true);
            fileTransmitterThread.start();
        }
        fileTransmitter.addFile(FILE_DATA_SERVICE.getFileById(fileId), fileId);
    }

    private void downloadSmallFile(ChannelHandlerContext ctx, FileDataPackage pack) throws IOException {
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

    private void downloadBigFile(ChannelHandlerContext ctx, FileChunkPackage pack) throws IOException {
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

    private void sendFileList(ChannelHandlerContext ctx) throws IOException {
        Map<String, Long> fileMap = new HashMap<>();
        Map<String, Long> directoryMap = new HashMap<>();
        Long id = currentDirectory.getId();
        List<FileData> currentFileList = DIRECTORY_SERVICE.getFiles(id);
        List<Directory> currentDirectoryList = DIRECTORY_SERVICE.getNestedDirectories(id);
        currentFileList.forEach(fileData -> fileMap.put(Constants.getFileNamePrefix() + fileData.getName(), fileData.getId()));
        currentDirectoryList.forEach(directory -> directoryMap.put(Constants.getDirectoryNamePrefix() + directory.getPath().substring(currentDirectory.getPath().length() + 1), directory.getId()));
        String currentDirectoryName = currentDirectory.getPath().substring(MAIN_PATH.length());
        ctx.writeAndFlush(new CurrentDirectoryEntityList(fileMap, directoryMap, currentDirectoryName, currentDirectory.getId()));
    }

    public static String getMainPath() {
        return MAIN_PATH;
    }
}