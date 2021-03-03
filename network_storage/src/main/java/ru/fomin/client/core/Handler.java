package ru.fomin.client.core;

import ru.fomin.client.gui.controllers.AuthenticationController;

import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Handler {

    private AuthenticationController authenticationController;

    public Handler(int port, String ip, AuthenticationController authenticationController) {
        this.authenticationController = authenticationController;
        try {
            Socket socket = new Socket(ip, port);
        } catch (IOException e) {
            authenticationController.changeIsConnected();
        }
    }
}
