package ru.fomin.services.handler_services;

import io.netty.channel.ChannelHandlerContext;
import ru.fomin.commands.CreatingAndUpdatingManipulationCommand;
import ru.fomin.commands.FileManipulationResponse;
import ru.fomin.entities.Directory;
import ru.fomin.services.db_services.DirectoryService;
import ru.fomin.services.db_services.FileDataService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainHandlerRequestDirectoryService {

    private final DirectoryService DIRECTORY_SERVICE;
    private final FileDataService FILE_DATA_SERVICE;

    public MainHandlerRequestDirectoryService(DirectoryService DIRECTORY_SERVICE, FileDataService FILE_DATA_SERVICE) {
        this.DIRECTORY_SERVICE = DIRECTORY_SERVICE;
        this.FILE_DATA_SERVICE = FILE_DATA_SERVICE;
    }

    public void requestDirectoryHandle(ChannelHandlerContext ctx, CreatingAndUpdatingManipulationCommand request, Directory currentDirectory) throws IOException {
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
}
