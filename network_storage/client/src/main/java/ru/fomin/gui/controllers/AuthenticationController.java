package ru.fomin.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.fomin.commands.AuthResult;
import ru.fomin.core.Commands;
import ru.fomin.core.MainHandler;

import static ru.fomin.util.ControllersUtil.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthenticationController {

    private static boolean isConnected;
    private static String login = "Dmitriy777";
    private static String password = "Dmitriy777";
    private Commands commands;

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

        field_login.setText(login);

        field_password.setText(password);

        btn_login.setOnAction(event -> authentication());

        btn_info.setOnAction(event -> showDeveloperInfo());

        btn_registration.setOnAction(event -> {
            if (connect()) {
                showAndHideStages("/fxml/registration.fxml", btn_registration);
            }
        });

        btnTCP_IP.setOnAction(event -> showAndHideStages("/fxml/connaction_properties.fxml", btnTCP_IP));

        commands=MainHandler.getCommands();

        MainHandler.setAuthenticationController(this);
    }

    private boolean connect() {
        if (!isConnected) {
            isConnected = true;
            try {
                commands.connect();
            } catch (IOException e) {
                isConnected = false;
                //e.printStackTrace();
                showConnectionError();
                return false;
            }
        }
        return true;
    }

    public static void changeIsConnected() {
        isConnected = isConnected ? false : true;
    }

    static void setAuthenticationData(String login, String password) {
        AuthenticationController.login = login;
        AuthenticationController.password = password;
    }

    private void authentication() {
        if (!connect()) {
            return;
        }
        String login = field_login.getText();
        String password = field_password.getText();
        if (!(hasText(login) && hasText(password))) {
            field_password.setText("");
            showErrorMessage("All field should be fill");
            return;
        }
        commands.authentication(login, password);
    }

    public void handleResponse(AuthResult.Result result) {
        if (result == AuthResult.Result.OK_AUTH) {
            showAndHideStages("/fxml/main_panel.fxml", btn_login);
        } else {
            field_login.setText("");
            field_password.setText("");
            showErrorMessage("Invalid login or password");
        }
    }

    public static boolean isConnected() {
        return isConnected;
    }
}