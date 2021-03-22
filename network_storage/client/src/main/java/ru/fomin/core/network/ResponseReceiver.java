package ru.fomin.core.network;

import javafx.application.Platform;
import ru.fomin.classes.FileChunkDownloader;
import ru.fomin.commands.AuthResult;
import ru.fomin.commands.CurrentDirectoryEntityList;
import ru.fomin.commands.DataPackage;
import ru.fomin.commands.FileManipulationResponse;
import ru.fomin.core.handlers.ResponseHandler;
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

public class ResponseReceiver implements Runnable {

    private final NetworkConnection networkConnection;
    private final ResponseHandler responseHandler;
    private final Map<Long, Path> downloadingFilesMap;
    private static final FileChunkDownloader FILE_CHUNK_DOWNLOADER = new FileChunkDownloader();

    public ResponseReceiver() {
        networkConnection=NetworkConnection.getInstance();
        responseHandler = ResponseHandler.getInstance();
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
                responseHandler.handleResponse(authResult);
            } else if (response instanceof CurrentDirectoryEntityList) {
                CurrentDirectoryEntityList com = (CurrentDirectoryEntityList) response;
                responseHandler.updateDirectoryEntity(com);
            } else if (response instanceof FileManipulationResponse) {
                responseHandler.getFileManipulationResponse((FileManipulationResponse) response);
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
            responseHandler.downloadingError(fileName);
        }
        responseHandler.downloadingSuccessful(fileName);
    }

    private void downloadBigFile(FileChunkPackage pack)  {
        String fileName = pack.getFilename();
        Runnable action = () -> responseHandler.downloadingSuccessful(fileName);
        Path path =downloadingFilesMap.get(pack.getDirectoryId());
        try {
            FILE_CHUNK_DOWNLOADER.writeFileChunk(pack, action, path);
        } catch (IOException e) {
            responseHandler.downloadingError(fileName);
        }
    }

    public void putDownloadingFilesMap(Long id, Path path) {
        downloadingFilesMap.put(id, path);
    }
}
