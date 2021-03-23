package ru.fomin.services.handler_services;

import io.netty.channel.ChannelHandlerContext;
import ru.fomin.commands.CreatingAndUpdatingManipulationRequest;
import ru.fomin.commands.FileManipulationResponse;
import ru.fomin.entities.Directory;
import ru.fomin.services.db_services.DirectoryService;
import ru.fomin.services.db_services.FileDataService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Service for process CreatingAndUpdatingManipulationRequest message from client.
 */
public class MainHandlerRequestDirectoryService {

    //services
    private final DirectoryService directoryService;
    private final FileDataService fileDataService;

    public MainHandlerRequestDirectoryService(DirectoryService directoryService, FileDataService fileDataService) {
        this.directoryService = directoryService;
        this.fileDataService = fileDataService;
    }

    /**
     * Verifies type of request and processes it.
     */
    public void requestDirectoryHandle(ChannelHandlerContext ctx, CreatingAndUpdatingManipulationRequest request) throws IOException {
        String newName = request.getNewName();
        Long id = request.getId();
        switch (request.getType()) {
            //create new directory
            case CREATE:
                Long newDirectoryId;
                Directory parentDirectory = directoryService.getDirectoryById(id);
                String newDirectory = parentDirectory.getPath() + File.separator + newName;
                //This method returns '-1' if directory with this name already exist into this parent directory
                if ((newDirectoryId = directoryService.createDirectory(parentDirectory, newDirectory)) != -1) {
                    Files.createDirectory(Paths.get(newDirectory));
                    ctx.writeAndFlush(new FileManipulationResponse(FileManipulationResponse.Response.DIR_CREATED, newName, newDirectoryId));
                } else {
                    ctx.writeAndFlush(new FileManipulationResponse(FileManipulationResponse.Response.DIR_ALREADY_EXIST, newName));
                }
                break;
            case RENAME_DIR:
                Path currentDirectoryPath = directoryService.getDirectoryPathById(id);
                Path newDirectoryPath = Paths.get(directoryService.renameDirectory(id, newName));
                Files.move(currentDirectoryPath, newDirectoryPath);
                ctx.writeAndFlush(new FileManipulationResponse(FileManipulationResponse.Response.RENAME_DIR, newName, id));
                break;
            case RENAME_FILE:
                Path currentFilePath = fileDataService.getFilePathById(id);
                Path newFilePath = fileDataService.renameFileData(id, newName);
                Files.move(currentFilePath, newFilePath);
                ctx.writeAndFlush(new FileManipulationResponse(FileManipulationResponse.Response.RENAME_FILE, newName, id));
                break;
            default:
                System.out.println(String.format("Unknown response \"%s\" from server", request.getType()));
        }
    }
}
