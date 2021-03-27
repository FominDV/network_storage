package ru.fomin.services;

import javafx.application.Platform;
import javafx.scene.control.Labeled;
import ru.fomin.dto.*;

import ru.fomin.dto.requests.AuthRequest;
import ru.fomin.dto.requests.CreatingAndUpdatingManipulationRequest;
import ru.fomin.dto.requests.FileManipulationRequest;
import ru.fomin.network.NetworkConnection;

import static ru.fomin.util.ControllersUtil.*;

import java.io.*;
import java.nio.file.Paths;

/**
 * Service for creating requests for server and processing some other commands from controllers.
 */
public class RequestService {

    private static RequestService instance;
    private final NetworkConnection networkConnection;

    private RequestService() {
        networkConnection = NetworkConnection.getInstance();
    }

    public static RequestService getInstance() {
        if (instance == null) {
            instance = new RequestService();
        }
        return instance;
    }

    /**
     * Uploading file to server.
     */
    public void sendFile(File file, Long directoryId) {
        networkConnection.addFileToTransmitter(file, directoryId);
    }

    /**
     * Exit from anyway window to authentication window.
     *
     * @param labeled - Labeled of current window
     */
    public void exitToAuthentication(Labeled labeled) {
        networkConnection.closeConnection();
        Platform.runLater(() -> showAndHideStages("/fxml/authentication.fxml", labeled));
    }

    /**
     * Request list of files and nested directories of root directory from server.
     */
    public void getCurrentDirectoryEntity() {
        networkConnection.sendToServer(new FileManipulationRequest(FileManipulationRequest.Request.GET_FILES_LIST));
    }

    /**
     * Downloading file from server.
     *
     * @param id   - id of downloading file
     * @param path - path where file will be download
     */
    public void download(Long id, String path) {
        networkConnection.putDownloadingFilesMapToResponseHandler(id, Paths.get(path));
        networkConnection.sendToServer(new FileManipulationRequest(FileManipulationRequest.Request.DOWNLOAD, id));
    }

    /**
     * Remove file or directory on server.
     */
    public void delete(Long id, FileManipulationRequest.Request type) {
        networkConnection.sendToServer(new FileManipulationRequest(type, id));
    }

    /**
     * Registration of new account.
     */
    public void registration(String login, String password) {
        networkConnection.sendToServer(new AuthRequest(login, password, AuthRequest.RequestType.REGISTRATION));
    }

    public void authentication(String login, String password) {
        DataPackage com = new AuthRequest(login.trim(), password.trim(), AuthRequest.RequestType.AUTH);
        networkConnection.sendToServer(com);
    }

    public void createDir(String dirName, Long remoteDirectoryId) {
        networkConnection.sendToServer(new CreatingAndUpdatingManipulationRequest(dirName, remoteDirectoryId, CreatingAndUpdatingManipulationRequest.Type.CREATE));
    }

    /**
     * Renaming file or directory.
     */
    public void rename(String dirName, Long remoteDirectoryId, CreatingAndUpdatingManipulationRequest.Type type) {
        networkConnection.sendToServer(new CreatingAndUpdatingManipulationRequest(dirName, remoteDirectoryId, type));
    }
}
