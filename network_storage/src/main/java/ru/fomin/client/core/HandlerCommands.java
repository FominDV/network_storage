package ru.fomin.client.core;

import javafx.application.Platform;
import ru.fomin.client.gui.controllers.AuthenticationController;

import javafx.scene.control.Button;
import ru.fomin.common.KeyCommands;

import java.io.*;
import java.net.Socket;

import static ru.fomin.client.util.ControllersUtil.*;

public class HandlerCommands implements Commands {

    private static Commands commands;
    private AuthenticationController authenticationController;
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;
    static String ip = "127.0.0.1";
    static int port = 8189;

    public HandlerCommands(AuthenticationController authenticationController) {
        this.authenticationController = authenticationController;
        try {
            socket = new Socket(ip, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            commands = this;
        } catch (IOException e) {
            authenticationController.changeIsConnected();
        }
    }

    public String sendFile(String filename) {
        try {
            File file = new File(filename);
            if (file.isFile()) {
                out.writeUTF(KeyCommands.UPLOAD);
                out.writeUTF(file.getName());
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
        try {
            socket.close();
            authenticationController.changeIsConnected();
            Platform.runLater(() -> {
                showAndHideStages("/fxml/authentication.fxml", button);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public boolean download(String filename, String path) {
        int sizeOfPackage = KeyCommands.SIZE_OF_PACKAGE;
        try {
            out.writeUTF(KeyCommands.DOWNLOAD);
            out.writeUTF(filename);
            File file = new File(path + File.separator + filename);
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
            //for future statistical
            //out.writeUTF(KeyCommands.DONE);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String filename) {
        try {
            out.writeUTF(KeyCommands.DELETE);
            out.writeUTF(filename);
            return in.readUTF().equals(KeyCommands.DONE) ? true : false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String registration(String login, String password) {
        return KeyCommands.DUPLICATED_LOGIN;
    }

    public static Commands getCommands() {
        return commands;
    }

    public static void setConnectionProperties(String ip, int port) {
        HandlerCommands.ip = ip;
        HandlerCommands.port = port;
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
}
