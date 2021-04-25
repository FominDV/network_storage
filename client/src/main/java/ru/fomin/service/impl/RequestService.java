package ru.fomin.service.impl;

import javafx.application.Platform;
import javafx.scene.control.Labeled;
import ru.fomin.dto.*;

import ru.fomin.dto.requests.AuthRequest;
import ru.fomin.dto.requests.ChangePasswordRequest;
import ru.fomin.dto.requests.CreatingAndUpdatingManipulationRequest;
import ru.fomin.dto.requests.FileManipulationRequest;
import ru.fomin.enumeration.AuthAndRegRequest;
import ru.fomin.enumeration.CreatingAndUpdatingRequest;
import ru.fomin.enumeration.FileManipulateRequest;
import ru.fomin.network.impl.NetworkConnection;
import ru.fomin.service.AuthenticationService;
import ru.fomin.service.ChangingPasswordService;
import ru.fomin.service.MainPanelService;
import ru.fomin.service.RegistrationService;

import static ru.fomin.util.ControllersUtil.*;

import java.io.*;
import java.nio.file.Paths;

/**
 * Service for creating requests for server and processing some other commands from controllers.
 */
public class RequestService implements MainPanelService, RegistrationService, AuthenticationService, ChangingPasswordService {

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
        networkConnection.sendToServer(new FileManipulationRequest(FileManipulateRequest.GET_FILES_LIST));
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
        networkConnection.sendToServer(new FileManipulationRequest(FileManipulateRequest.DOWNLOAD, id));
    }

    /**
     * Remove file or directory on server.
     */
    @Override
    public void delete(Long id, FileManipulateRequest type) {
        networkConnection.sendToServer(new FileManipulationRequest(type, id));
    }

    /**
     * Registration of new account.
     */
    @Override
    public void registration(String login, String password) {
        networkConnection.sendToServer(new AuthRequest(password, login, AuthAndRegRequest.REGISTRATION));
    }

    @Override
    public void authentication(String login, String password) {
        DataPackage com = new AuthRequest(password.trim(), login.trim(), AuthAndRegRequest.AUTH);
        networkConnection.sendToServer(com);
    }

    @Override
    public void createDir(String dirName, Long remoteDirectoryId) {
        networkConnection.sendToServer(new CreatingAndUpdatingManipulationRequest(dirName, remoteDirectoryId, CreatingAndUpdatingRequest.CREATE));
    }

    /**
     * Renaming file or directory.
     */
    @Override
    public void rename(String dirName, Long remoteDirectoryId, CreatingAndUpdatingRequest type) {
        networkConnection.sendToServer(new CreatingAndUpdatingManipulationRequest(dirName, remoteDirectoryId, type));
    }

    @Override
    public void moveToNestedDirectory(Long id) {
        networkConnection.sendToServer(new FileManipulationRequest(FileManipulateRequest.INTO_DIR, id));
    }

    @Override
    public void moveFromCurrentDirectory(Long id) {
        networkConnection.sendToServer(new FileManipulationRequest(FileManipulateRequest.OUT_DIR, id));
    }

    @Override
    public void changePassword(String currentPassword, String newPassword) {
        networkConnection.sendToServer(new ChangePasswordRequest(newPassword, currentPassword));
    }
}
