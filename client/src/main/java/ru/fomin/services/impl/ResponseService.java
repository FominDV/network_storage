package ru.fomin.services.impl;

import javafx.application.Platform;
import ru.fomin.classes.FileChunkDownloader;
import ru.fomin.dto.responses.AuthResult;
import ru.fomin.dto.responses.ChangePasswordResponse;
import ru.fomin.dto.responses.CurrentDirectoryEntityList;
import ru.fomin.dto.DataPackage;
import ru.fomin.dto.responses.FileManipulationResponse;
import ru.fomin.dto.file_packages.FileChunkPackage;
import ru.fomin.dto.file_packages.FileDataPackage;
import ru.fomin.controllers.AuthenticationController;
import ru.fomin.controllers.MainPanelController;
import ru.fomin.controllers.RegistrationController;
import ru.fomin.controllers.UpdatePasswordController;
import ru.fomin.services.NetworkConnectionService;
import ru.fomin.services.ResponseProcessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static ru.fomin.util.ControllersUtil.*;

/**
 * Service for processing messages from server.
 */
public class ResponseService implements ResponseProcessor, NetworkConnectionService {

    private static ResponseService instance;
    private static AuthenticationController authenticationController;
    private static MainPanelController mainPanelController;
    private static RegistrationController registrationController;
    private static UpdatePasswordController updatePasswordController;

    private static final FileChunkDownloader FILE_CHUNK_DOWNLOADER = new FileChunkDownloader();

    //contains id of downloading file anf path of this file on client side.
    private final Map<Long, Path> downloadingFilesMap;

    private ResponseService() {
        downloadingFilesMap = new HashMap<>();
    }

    public static ResponseService getInstance() {
        if (instance == null) {
            instance = new ResponseService();
        }
        return instance;
    }

    /**
     * Closes all windows and shows the authentication window.
     */
    @Override
    public void exitOnFatalConnectionError() {
        if (mainPanelController != null) {
            Platform.runLater(() -> hideWindow(mainPanelController.getLabeled()));
        }
        if (registrationController != null) {
            Platform.runLater(() -> hideWindow(registrationController.getLabeled()));
        }
        if (updatePasswordController != null) {
            Platform.runLater(() -> hideWindow(updatePasswordController.getBtn_info()));
        }
        Platform.runLater(() -> {
            showStage("/fxml/authentication.fxml");
            showErrorMessage("Connection was lost");
        });
    }

    /**
     * Add new information to downloading file.
     *
     * @param id   - id of file
     * @param path - path of file on client side
     */
    @Override
    public void putDownloadingFilesMap(Long id, Path path) {
        downloadingFilesMap.put(id, path);
    }

    @Override
    public void clearDownloadingFilesMap() {
        downloadingFilesMap.clear();
    }

    /**
     * Recognition message from server and delegate it to needed methods.
     */
    @Override
    public void processResponse(DataPackage response) {
        Platform.runLater(() -> {
            if (response instanceof AuthResult) {
                AuthResult authResult = (AuthResult) response;
                handleAuthResponse(authResult);
            } else if (response instanceof CurrentDirectoryEntityList) {
                CurrentDirectoryEntityList com = (CurrentDirectoryEntityList) response;
                updateDirectoryEntity(com);
            } else if (response instanceof FileManipulationResponse) {
                getFileManipulationResponse((FileManipulationResponse) response);
            } else if (response instanceof FileDataPackage) {
                downloadSmallFile((FileDataPackage) response);
            } else if (response instanceof FileChunkPackage) {
                downloadBigFile((FileChunkPackage) response);
            } else if (response instanceof ChangePasswordResponse) {
                updatePasswordController.handleResponse(((ChangePasswordResponse) response).isSuccessful());
            }
        });
    }

    /**
     * Saving file by one chunk from server.
     */
    private void downloadSmallFile(FileDataPackage pack) {
        String fileName = pack.getFilename();
        Path path = Paths.get(downloadingFilesMap.get(pack.getDirectoryId()) + File.separator + fileName);
        downloadingFilesMap.remove(fileName);
        try {
            Files.write(path, pack.getData());
        } catch (IOException e) {
            downloadingError(fileName);
        }
        downloadingSuccessful(fileName);
    }

    /**
     * Saving chunk of saving file from server.
     */
    private void downloadBigFile(FileChunkPackage pack) {
        String fileName = pack.getFilename();
        Long id = pack.getId();
        Runnable action = () -> {
            downloadingFilesMap.remove(id);
            downloadingSuccessful(fileName);
        };
        //getting path of downloading file from map by id of file
        Path path = downloadingFilesMap.get(pack.getId());
        try {
            FILE_CHUNK_DOWNLOADER.writeFileChunk(pack, action, path);
        } catch (IOException e) {
            downloadingError(fileName);
        }
    }

    /**
     * Handling informational message from server about authentication.
     */
    private void handleAuthResponse(AuthResult authResult) {
        AuthResult.Result result = authResult.getResult();
        if (result == AuthResult.Result.FAIL_AUTH || result == AuthResult.Result.OK_AUTH) {
            authenticationController.handleResponse(result);
        } else {
            registrationController.handleResponse(result, authResult.getLogin());
        }
    }

    /**
     * Updating list of current directories and files.
     */
    private void updateDirectoryEntity(CurrentDirectoryEntityList com) {
        mainPanelController.updateDirectoryEntity(com);
    }

    /**
     * Handling informational message from server about manipulations bu files.
     */
    private void getFileManipulationResponse(FileManipulationResponse response) {
        mainPanelController.getFileManipulationResponse(response);
    }

    private void downloadingSuccessful(String filename) {
        showInfoMessage(String.format("Downloading file \"%s\" is successfully", filename));
    }

    private void downloadingError(String fileName) {
        showErrorMessage(String.format("Downloading file \"%s\" is failed", fileName));
    }

    public static void setMainPanelController(MainPanelController mainPanelController) {
        ResponseService.mainPanelController = mainPanelController;
    }

    public static void setRegistrationController(RegistrationController registrationController) {
        ResponseService.registrationController = registrationController;
    }

    public static void setUpdatePasswordController(UpdatePasswordController updatePasswordController) {
        ResponseService.updatePasswordController = updatePasswordController;
    }

    public static void setAuthenticationController(AuthenticationController authenticationController) {
        ResponseService.authenticationController = authenticationController;
    }
}
