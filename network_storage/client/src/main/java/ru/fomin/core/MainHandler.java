package ru.fomin.core;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import ru.fomin.commands.*;
import ru.fomin.gui.controllers.AuthenticationController;

import javafx.scene.control.Button;
import ru.fomin.gui.controllers.MainPanelController;
import ru.fomin.gui.controllers.RegistrationController;
import ru.fomin.gui.controllers.UpdatePasswordController;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static ru.fomin.util.ControllersUtil.*;

import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;

public class MainHandler implements Commands {

    private static Commands commands;
    private final NetworkConnection networkConnection;
    private static AuthenticationController authenticationController;
    private static MainPanelController mainPanelController;
    private static RegistrationController registrationController;
    private static UpdatePasswordController updatePasswordController;

    private MainHandler() {
        networkConnection = NetworkConnection.getInstance(this);
    }

    public static Commands getCommands() {
        if (commands == null) {
            commands = new MainHandler();
        }
        return commands;
    }

    @Override
    public void sendFile(File file, Long directoryId) {
        networkConnection.addFileToTransmitter(file, directoryId);
    }

    @Override
    public void exitToAuthentication(Button button) {
        networkConnection.closeConnection();
        Platform.runLater(() -> showAndHideStages("/fxml/authentication.fxml", button));
    }

    @Override
    public void getCurrentDirectoryEntity() {
        networkConnection.sendToServer(new FileManipulationRequest(FileManipulationRequest.Request.GET_FILES_LIST));
    }

    @Override
    public void download(Long id, String path) {
        networkConnection.putDownloadingFilesMapToResponseHandler(id, Paths.get(path));
        networkConnection.sendToServer(new FileManipulationRequest(FileManipulationRequest.Request.DOWNLOAD, id));
    }

    @Override
    public void delete(Long id, FileManipulationRequest.Request type) {
        networkConnection.sendToServer(new FileManipulationRequest(type, id));
    }

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

    @Override
    public void rename(String dirName, Long remoteDirectoryId, CreatingAndUpdatingManipulationRequest.Type type) {
        networkConnection.sendToServer(new CreatingAndUpdatingManipulationRequest(dirName, remoteDirectoryId, type));
    }

    @Override
    public void connect() throws IOException {
        networkConnection.connect();
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

    public static void setMainPanelController(MainPanelController mainPanelController) {
        MainHandler.mainPanelController = mainPanelController;
    }

    public static void setRegistrationController(RegistrationController registrationController) {
        MainHandler.registrationController = registrationController;
    }

    public static void setUpdatePasswordController(UpdatePasswordController updatePasswordController) {
        MainHandler.updatePasswordController = updatePasswordController;
    }

    public static void setAuthenticationController(AuthenticationController authenticationController) {
        MainHandler.authenticationController = authenticationController;
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

}
