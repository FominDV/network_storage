package ru.fomin.services;

import javafx.application.Platform;
import ru.fomin.classes.FileChunkDownloader;
import ru.fomin.commands.AuthResult;
import ru.fomin.commands.CurrentDirectoryEntityList;
import ru.fomin.commands.DataPackage;
import ru.fomin.commands.FileManipulationResponse;
import ru.fomin.file_packages.FileChunkPackage;
import ru.fomin.file_packages.FileDataPackage;
import ru.fomin.gui.controllers.AuthenticationController;
import ru.fomin.gui.controllers.MainPanelController;
import ru.fomin.gui.controllers.RegistrationController;
import ru.fomin.gui.controllers.UpdatePasswordController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static ru.fomin.util.ControllersUtil.*;

public class ResponseService {

    private static ResponseService instance;
    private static AuthenticationController authenticationController;
    private static MainPanelController mainPanelController;
    private static RegistrationController registrationController;
    private static UpdatePasswordController updatePasswordController;

    private static final FileChunkDownloader FILE_CHUNK_DOWNLOADER = new FileChunkDownloader();

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

    public void processResponse(DataPackage response) {
        Platform.runLater(() -> {
            if (response instanceof AuthResult) {
                AuthResult authResult = (AuthResult) response;
                handleResponse(authResult);
            } else if (response instanceof CurrentDirectoryEntityList) {
                CurrentDirectoryEntityList com = (CurrentDirectoryEntityList) response;
                updateDirectoryEntity(com);
            } else if (response instanceof FileManipulationResponse) {
                getFileManipulationResponse((FileManipulationResponse) response);
            } else if (response instanceof FileDataPackage) {
                downloadSmallFile((FileDataPackage) response);
            } else if (response instanceof FileChunkPackage) {
                downloadBigFile((FileChunkPackage) response);
            }
        });
    }

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

    private void downloadBigFile(FileChunkPackage pack) {
        String fileName = pack.getFilename();
        Runnable action = () -> {
            downloadingFilesMap.remove(fileName);
            downloadingSuccessful(fileName);
        };
        Path path = downloadingFilesMap.get(pack.getDirectoryId());
        try {
            FILE_CHUNK_DOWNLOADER.writeFileChunk(pack, action, path);
        } catch (IOException e) {
            downloadingError(fileName);
        }
    }

    public void putDownloadingFilesMap(Long id, Path path) {
        downloadingFilesMap.put(id, path);
    }

    private void clearDownloadingFilesMap() {
        downloadingFilesMap.clear();
    }

    public static void exitOnFatalConnectionError() {
        if (mainPanelController != null) {
            Platform.runLater(() -> hideWindow(mainPanelController.getLabeled()));
        }
        if (registrationController != null) {
            Platform.runLater(() -> hideWindow(registrationController.getLabeled()));
        }
        if (updatePasswordController != null) {
            Platform.runLater(() -> hideWindow(updatePasswordController.getLabeled()));
        }
        Platform.runLater(() -> {
            showStage("/fxml/authentication.fxml");
            showErrorMessage("Connection was lost");
        });
    }

    public void handleResponse(AuthResult authResult) {
        AuthResult.Result result = authResult.getResult();
        if (result == AuthResult.Result.FAIL_AUTH || result == AuthResult.Result.OK_AUTH) {
            authenticationController.handleResponse(result);
        } else {
            registrationController.handleResponse(result, authResult.getLogin());
        }
    }

    public void updateDirectoryEntity(CurrentDirectoryEntityList com) {
        mainPanelController.updateDirectoryEntity(com);
    }

    public void getFileManipulationResponse(FileManipulationResponse response) {
        mainPanelController.getFileManipulationResponse(response);
    }

    public void downloadingSuccessful(String filename) {
        showInfoMessage(String.format("Downloading file \"%s\" is successfully", filename));
    }

    public void downloadingError(String fileName) {
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
