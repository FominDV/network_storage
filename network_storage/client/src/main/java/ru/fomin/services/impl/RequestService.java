package ru.fomin.services.impl;

import javafx.application.Platform;
import javafx.scene.control.Labeled;
import ru.fomin.dto.*;

import ru.fomin.dto.requests.AuthRequest;
import ru.fomin.dto.requests.CreatingAndUpdatingManipulationRequest;
import ru.fomin.dto.requests.FileManipulationRequest;
import ru.fomin.network.impl.NetworkConnection;
import ru.fomin.services.AuthenticationService;
import ru.fomin.services.ChangingPasswordService;
import ru.fomin.services.MainPanelService;
import ru.fomin.services.RegistrationService;

import static ru.fomin.util.ControllersUtil.*;

import java.io.*;
import java.nio.file.Paths;

/**
 * Service for creating requests for server and processing some other commands from controllers.
 */
public class ServiceService implements MainPanelService, RegistrationService, AuthenticationService, ChangingPasswordService {

    private static ServiceService instance;
    private final NetworkConnection networkConnection;

    private ServiceService() {
        networkConnection = NetworkConnection.getInstance();
    }

    public static ServiceService getInstance() {
        if (instance == null) {
            instance = new ServiceService();
        }
        return instance;
    }

    /**
     * Uploading file to server.
     */
    @Override
    public void sendFile(File file, Long directoryId) {
        networkConnection.addFileToTransmitter(file, directoryId);
    }

    /**
     * Exit from anyway window to authentication window.
     *
     * @param labeled - Labeled of current window
     */
    @Override
    public void exitToAuthentication(Labeled labeled) {
        networkConnection.closeConnection();
        Platform.runLater(() -> showAndHideStages("/fxml/authentication.fxml", labeled));
    }

    /**
     * Request list of files and nested directories of root directory from server.
     */
    @Override
    public void getCurrentDirectoryEntity() {
        networkConnection.sendToServer(new FileManipulationRequest(FileManipulationRequest.Request.GET_FILES_LIST));
    }

    /**
     * Downloading file from server.
     *
     * @param id   - id of downloading file
     * @param path - path where file will be download
     */
    @Override
    public void download(Long id, String path) {
        networkConnection.putDownloadingFilesMapToResponseHandler(id, Paths.get(path));
        networkConnection.sendToServer(new FileManipulationRequest(FileManipulationRequest.Request.DOWNLOAD, id));
    }

    /**
     * Remove file or directory on server.
     */
    @Override
    public void delete(Long id, FileManipulationRequest.Request type) {
        networkConnection.sendToServer(new FileManipulationRequest(type, id));
    }

    /**
     * Registration of new account.
     */
    @Override
    public void registration(String login, String password) {
        networkConnection.sendToServer(new AuthRequest(login, password, AuthRequest.RequestType.REGISTRATION));
    }

    @Override
    public void authentication(String login, String password) {
        DataPackage com = new AuthRequest(login.trim(), password.trim(), AuthRequest.RequestType.AUTH);
        networkConnection.sendToServer(com);
    }

    @Override
    public void createDir(String dirName, Long remoteDirectoryId) {
        networkConnection.sendToServer(new CreatingAndUpdatingManipulationRequest(dirName, remoteDirectoryId, CreatingAndUpdatingManipulationRequest.Type.CREATE));
    }

    /**
     * Renaming file or directory.
     */
    @Override
    public void rename(String dirName, Long remoteDirectoryId, CreatingAndUpdatingManipulationRequest.Type type) {
        networkConnection.sendToServer(new CreatingAndUpdatingManipulationRequest(dirName, remoteDirectoryId, type));
    }
}
