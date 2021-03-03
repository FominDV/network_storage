package ru.fomin.client.core;

import javafx.application.Application;
import javafx.application.Platform;
import ru.fomin.client.gui.controllers.AuthenticationController;
import ru.fomin.client.util.ControllersUtil;

import javafx.scene.control.Button;
import java.io.*;
import java.net.ContentHandler;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static ru.fomin.client.util.ControllersUtil.showAndHideStages;

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
            if (file.exists()) {
                out.writeUTF("upload");
                out.writeUTF(file.getName());
                long length = file.length();
                out.writeLong(length);
                FileInputStream fis = new FileInputStream(file);
                int read = 0;
                byte[] buffer = new byte[256];
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
}
