package ru.fomin.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.fomin.client.core.HandlerCommands;

import static ru.fomin.client.util.ControllersUtil.*;

import java.net.URL;
import java.util.ResourceBundle;

public class AuthenticationController {
    private boolean isConnected = false;

    @FXML
    private ResourceBundle resources;
    @FXML
    private Button btn_info;

    @FXML
    private URL location;

    @FXML
    private TextField field_login;

    @FXML
    private Button btn_login;

    @FXML
    private Button btn_registration;

    @FXML
    private PasswordField field_password;
    @FXML
    private Button btnTCP_IP;

    @FXML
    void initialize() {
        btn_login.setOnAction(event -> {
            connect();
            showAndHideStages("/fxml/main_panel.fxml", btn_login);
        });
        btn_registration.setOnAction(event -> {
            connect();

        });

        btnTCP_IP.setOnAction(event -> {
        });
        btn_info.setOnAction(event -> showDeveloperInfo());
    }

    private void connect() {
        if (!isConnected) {
            isConnected = true;
            new HandlerCommands(this);
        }
    }

    public void changeIsConnected() {
        isConnected = isConnected ? false : true;
    }

}