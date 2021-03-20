package ru.fomin.core;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import ru.fomin.*;
import ru.fomin.gui.controllers.AuthenticationController;

import javafx.scene.control.Button;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static ru.fomin.util.ControllersUtil.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;

public class HandlerCommands implements Commands {

    private final ExecutorService executorService;
    private static Commands commands;
    private AuthenticationController authenticationController;
    private ObjectEncoderOutputStream out;
    private ObjectDecoderInputStream in;
    private SocketAddress addr;
    private Socket socket;
    static String ip = "127.0.0.1";
    static int port = 8189;
    private static final int MAX_OBJ_SIZE = 10 * 1024 * 1024;

    public HandlerCommands(AuthenticationController authenticationController) {
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
            executorService.execute(new ResponseHandler(this));
        } catch (IOException e) {
            authenticationController.changeIsConnected();
            e.printStackTrace();
        }
    }

    public String sendFile(String filePath, String fileName) {
        try {
            File file = new File(filePath);
            if (file.isFile()) {
                out.writeUTF(KeyCommands.UPLOAD);
                out.writeUTF(fileName);
                if (in.readUTF().equals(KeyCommands.ALREADY_EXIST)) {
                    return KeyCommands.ALREADY_EXIST;
                }
                long length = file.length();
                out.writeLong(length);
                FileInputStream fis = new FileInputStream(file);
                int read = 0;
                byte[] buffer = new byte[KeyCommands.SIZE_OF_PACKAGE];
                while ((read = fis.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                out.flush();
                String status = in.readUTF();
                return status;
            } else {
                return "File is not exists";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Something error";
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
    public String[] getFiles() {
        String files = "";
        try {
            out.writeUTF(KeyCommands.GET_FILES);
            files = in.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files.isEmpty() ? new String[0] : files.split(KeyCommands.DELIMITER);
    }

    @Override
    public boolean download(Long id, String path, String fileName) {
        int sizeOfPackage = KeyCommands.SIZE_OF_PACKAGE;
        try {
            out.writeUTF(KeyCommands.DOWNLOAD);
            out.writeLong(id);
            File file = new File(path + File.separator + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            long size = in.readLong();
            long countOfPackages = (size + sizeOfPackage - 1) / sizeOfPackage;
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] buffer = new byte[sizeOfPackage];
            for (long i = 0; i < countOfPackages; i++) {
                int read = in.read(buffer);
                fileOutputStream.write(buffer, 0, read);
            }
            fileOutputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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
    public String getCurrentDirectory() {
        try {
            out.writeUTF(KeyCommands.GET_CURRENT_DIR);
            return in.readUTF();
        } catch (IOException e) {
            showConnectionError();
            closeConnection();
        }
        return KeyCommands.ERROR;
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

    public DataPackage getResponseFromServer()  {
        try
        {
            Object obj = in.readObject();
            return (DataPackage) obj;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
           return null;
    }

    public void authenticationResponse(AuthResult authResult){
            authenticationController.authenticationResponse(authResult);
    }

}
