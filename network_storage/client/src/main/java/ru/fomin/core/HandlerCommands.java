package ru.fomin.core;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import ru.fomin.*;
import ru.fomin.gui.controllers.AuthenticationController;

import javafx.scene.control.Button;
import ru.fomin.gui.controllers.MainPanelController;
import ru.fomin.need.commands.*;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static ru.fomin.util.ControllersUtil.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;

public class HandlerCommands implements Commands {

    private final ExecutorService executorService;
    private static Commands commands;
    private final FileTransmitter fileTransmitter;
    private final ResponseHandler responseHandler;

    private AuthenticationController authenticationController;
    private MainPanelController mainPanelController;

    private ObjectEncoderOutputStream out;
    private ObjectDecoderInputStream in;
    private SocketAddress addr;
    private Socket socket;
    static String ip = "127.0.0.1";
    static int port = 8189;
    private static final int MAX_OBJ_SIZE = 10 * 1024 * 1024;

    public HandlerCommands(AuthenticationController authenticationController) throws IOException {
        this.authenticationController = authenticationController;
        executorService = newFixedThreadPool(2);
        socket = new Socket();
        addr = new InetSocketAddress(ip, port);
        try {
            socket = new Socket(ip, port);
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            out = new ObjectEncoderOutputStream(os);
            in = new ObjectDecoderInputStream(is, MAX_OBJ_SIZE);
            commands = this;
            responseHandler = new ResponseHandler(this);
            executorService.execute(responseHandler);
            fileTransmitter = new FileTransmitter(this);
            executorService.execute(fileTransmitter);
        } catch (IOException e) {
            throw new IOException();
        }
    }

    @Override
    public void sendFile(File file, Long directoryId) {
        fileTransmitter.addFile(file, directoryId);
    }

    @Override
    public void exitToAuthentication(Button button) {
        closeConnection();
        authenticationController.changeIsConnected();
        Platform.runLater(() -> {
            showAndHideStages("/fxml/authentication.fxml", button);
        });
    }

    @Override
    public void getCurrentDirectoryEntity() {
        try {
            out.writeObject(new FileManipulationRequest(FileManipulationRequest.Request.GET_FILES_LIST));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void download(Long id, String path) {
        responseHandler.putDownloadingFilesMap(id, Paths.get(path));
        sendToServer(new FileManipulationRequest(FileManipulationRequest.Request.DOWNLOAD, id));
    }

    @Override
    public boolean delete(Long id, String type) {
        try {
            out.writeUTF(type);
            out.writeLong(id);
            return in.readUTF().equals(KeyCommands.DONE) ? true : false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Should return KeyCommands.DONE, KeyCommands.DUPLICATED_LOGIN or any error
    @Override
    public String registration(String login, String password) {
        try {
            out.writeUTF(KeyCommands.REGISTRATION);
            out.writeUTF(login);
            out.writeUTF(password);
            return in.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
            return KeyCommands.ERROR;
        }
    }

    @Override
    public void authentication(String login, String password) {
        DataPackage com = new AuthCommand(login.trim(), password.trim());
        sendToServer(com);
    }

    @Override
    public String createDir(String dirName) {
        try {
            out.writeUTF(KeyCommands.CREATE_DIRECTORY);
            out.writeUTF(dirName);
            return in.readUTF();
        } catch (IOException e) {
            showConnectionError();
            closeConnection();
        }
        return KeyCommands.ERROR;
    }

    private void closeConnection() {
        try {
            executorService.shutdownNow();
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Commands getCommands() {
        return commands;
    }

    public static String getIp() {
        return ip;
    }

    public static int getPort() {
        return port;
    }

    public static void setIp(String ip) {
        HandlerCommands.ip = ip;
    }

    public static void setPort(int port) {
        HandlerCommands.port = port;
    }

    ////////////////////////////////////
    public void sendToServer(DataPackage data) {
        try {
            out.writeObject(data);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public DataPackage getResponseFromServer() {
        try {
            Object obj = in.readObject();
            return (DataPackage) obj;
        } catch (IOException | ClassNotFoundException e) {
            authenticationController.changeIsConnected();
            closeConnection();
        }
        return null;
    }

    public void authenticationResponse(AuthResult authResult) {
        authenticationController.authenticationResponse(authResult);
    }

    public void updateDirectoryEntity(CurrentDirectoryEntityList com) {
        mainPanelController.updateDirectoryEntity(com);
    }

    @Override
    public void setMainPanelController(MainPanelController mainPanelController) {
        this.mainPanelController = mainPanelController;
    }

    public void getFileManipulationResponse(FileManipulationResponse response) {
        mainPanelController.getFileManipulationResponse(response);
    }

    public void downloadingSuccessful(String filename){
       showInfoMessage(String.format("Downloading file \"%s\" is successfully",filename));
    }

    public void downloadingError(String fileName) {
        showErrorMessage(String.format("Downloading file \"%s\" is failed",fileName));
    }
}
