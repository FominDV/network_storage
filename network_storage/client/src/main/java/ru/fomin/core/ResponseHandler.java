package ru.fomin.core;

import javafx.application.Platform;
import ru.fomin.classes.FileChunkDownloader;
import ru.fomin.commands.AuthResult;
import ru.fomin.commands.CurrentDirectoryEntityList;
import ru.fomin.commands.DataPackage;
import ru.fomin.commands.FileManipulationResponse;
import ru.fomin.file_packages.FileChunkPackage;
import ru.fomin.file_packages.FileDataPackage;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.currentThread;

public class ResponseHandler implements Runnable {

    private final NetworkConnection networkConnection;
    private final MainHandler mainHandler;
    private final Map<Long, Path> downloadingFilesMap;
    private static final FileChunkDownloader FILE_CHUNK_DOWNLOADER = new FileChunkDownloader();

    public ResponseHandler(NetworkConnection networkConnection, MainHandler mainHandler) {
        this.networkConnection=networkConnection;
        this.mainHandler=mainHandler;
        downloadingFilesMap = new HashMap<>();
    }

    @Override
    public void run() {
        while (!currentThread().isInterrupted()) {
            DataPackage response = networkConnection.getResponseFromServer();
            if (response != null) {
                processResponse(response);
            }
        }
    }

    private void processResponse(DataPackage response) {
        Platform.runLater(() -> {
            if (response instanceof AuthResult) {
                AuthResult authResult = (AuthResult) response;
                mainHandler.handleResponse(authResult);
            } else if (response instanceof CurrentDirectoryEntityList) {
                CurrentDirectoryEntityList com = (CurrentDirectoryEntityList) response;
                mainHandler.updateDirectoryEntity(com);
            } else if (response instanceof FileManipulationResponse) {
                mainHandler.getFileManipulationResponse((FileManipulationResponse) response);
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
            mainHandler.downloadingError(fileName);
        }
        mainHandler.downloadingSuccessful(fileName);
    }

    private void downloadBigFile(FileChunkPackage pack)  {
        String fileName = pack.getFilename();
        Runnable action = () -> mainHandler.downloadingSuccessful(fileName);
        Path path =downloadingFilesMap.get(pack.getDirectoryId());
        try {
            FILE_CHUNK_DOWNLOADER.writeFileChunk(pack, action, path);
        } catch (IOException e) {
            mainHandler.downloadingError(fileName);
        }
    }

    public void putDownloadingFilesMap(Long id, Path path) {
        downloadingFilesMap.put(id, path);
    }
}
