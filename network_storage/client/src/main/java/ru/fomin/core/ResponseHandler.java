package ru.fomin.core;

import io.netty.channel.ChannelHandlerContext;
import javafx.application.Platform;
import ru.fomin.need.classes.FileChunkDownloader;
import ru.fomin.need.commands.*;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.currentThread;

public class ResponseHandler implements Runnable {

    private final HandlerCommands handlerCommands;
    private final Map<Long, Path> downloadingFilesMap;
    private static final FileChunkDownloader FILE_CHUNK_DOWNLOADER = new FileChunkDownloader();

    public ResponseHandler(HandlerCommands handlerCommands) {
        this.handlerCommands = handlerCommands;
        downloadingFilesMap = new HashMap<>();
    }

    @Override
    public void run() {
        while (!currentThread().isInterrupted()) {
            DataPackage response = handlerCommands.getResponseFromServer();
            if (response != null) {
                processResponse(response);
            }
        }

    }

    private void processResponse(DataPackage response) {
        Platform.runLater(() -> {
            if (response instanceof AuthResult) {
                AuthResult authResult = (AuthResult) response;
                handlerCommands.authenticationResponse(authResult);
            } else if (response instanceof CurrentDirectoryEntityList) {
                CurrentDirectoryEntityList com = (CurrentDirectoryEntityList) response;
                handlerCommands.updateDirectoryEntity(com);
            } else if (response instanceof FileManipulationResponse) {
                handlerCommands.getFileManipulationResponse((FileManipulationResponse) response);
            } else if(response instanceof FileDataPackage){
                downloadSmallFile((FileDataPackage) response);
            } else if(response instanceof FileChunkPackage){
                downloadBigFile((FileChunkPackage) response);
            }
        });
    }

    private void downloadSmallFile( FileDataPackage pack)  {
        String fileName = pack.getFilename();
        Path path = Paths.get(downloadingFilesMap.get(pack.getDirectoryId()) + File.separator + fileName);
        downloadingFilesMap.remove(fileName);
        try {
            Files.write(path, pack.getData());
        } catch (IOException e) {
            handlerCommands.downloadingError(fileName);
        }
        handlerCommands.downloadingSuccessful(fileName);
    }

    private void downloadBigFile(FileChunkPackage pack)  {
        String fileName = pack.getFilename();
        Runnable action = () -> handlerCommands.downloadingSuccessful(fileName);
        Path path =downloadingFilesMap.get(pack.getDirectoryId());
        try {
            FILE_CHUNK_DOWNLOADER.writeFileChunk(pack, action, path);
        } catch (IOException e) {
            handlerCommands.downloadingError(fileName);
        }
    }

    public void putDownloadingFilesMap(Long id, Path path) {
        downloadingFilesMap.put(id, path);
    }
}
