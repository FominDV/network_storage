package ru.fomin.core.handlers;

import javafx.application.Platform;
import ru.fomin.commands.*;

import javafx.scene.control.Button;
import ru.fomin.core.network.NetworkConnection;

import static ru.fomin.util.ControllersUtil.*;

import java.io.*;
import java.nio.file.Paths;

public class RequestHandler {

    private static RequestHandler instance;
    private final NetworkConnection networkConnection;


    private RequestHandler() {
        networkConnection = NetworkConnection.getInstance();
    }

    public static RequestHandler getInstance() {
        if (instance == null) {
            instance= new RequestHandler();
        }
        return instance;
    }

    public void sendFile(File file, Long directoryId) {
        networkConnection.addFileToTransmitter(file, directoryId);
    }

    public void exitToAuthentication(Button button) {
        networkConnection.closeConnection();
        Platform.runLater(() -> showAndHideStages("/fxml/authentication.fxml", button));
    }

    public void getCurrentDirectoryEntity() {
        networkConnection.sendToServer(new FileManipulationRequest(FileManipulationRequest.Request.GET_FILES_LIST));
    }

    public void download(Long id, String path) {
        networkConnection.putDownloadingFilesMapToResponseHandler(id, Paths.get(path));
        networkConnection.sendToServer(new FileManipulationRequest(FileManipulationRequest.Request.DOWNLOAD, id));
    }

    public void delete(Long id, FileManipulationRequest.Request type) {
        networkConnection.sendToServer(new FileManipulationRequest(type, id));
    }

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

    public void rename(String dirName, Long remoteDirectoryId, CreatingAndUpdatingManipulationRequest.Type type) {
        networkConnection.sendToServer(new CreatingAndUpdatingManipulationRequest(dirName, remoteDirectoryId, type));
    }
}
