package ru.fomin.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.fomin.enumeration.AuthAndRegResult;
import ru.fomin.util.encoder.Encoder;
import ru.fomin.factory.Factory;
import ru.fomin.network.Connection;
import ru.fomin.service.AuthenticationService;
import ru.fomin.service.impl.ResponseService;

import static ru.fomin.util.ControllersUtil.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Window of authentication.
 */
public class AuthenticationController {

    //initial values of login and password
    private static String login = "Dmitriy777";
    private static String password = "Dmitriy777";

    private static final Encoder encoder = Factory.getEncoder();

    private AuthenticationService authenticationService;
    private Connection connection;

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

        authenticationService = Factory.getAuthenticationRequest();

        connection = Factory.getConnection();

        ResponseService.setAuthenticationController(this);
    }

    /**
     * Create connection to server.
     *
     * @return - true if connection was created
     */
    private boolean connect() {
        try {
            connection.connect();
        } catch (IOException e) {
            showConnectionError();
            return false;
        }
        return true;
    }

    private void authentication() {
        if (!connect()) {
            return;
        }
        login = field_login.getText();
        password = field_password.getText();
        if (!(hasText(login) && hasText(password))) {
            field_password.clear();
            showErrorMessage("All field should be fill");
            return;
        }
        authenticationService.authentication(login, encoder.encode(password));
    }

    /**
     * Handling response from server.
     */
    public void handleResponse(AuthAndRegResult authAndRegResult) {
        if (authAndRegResult == AuthAndRegResult.OK_AUTH) {
            showAndHideStages("/fxml/main_panel.fxml", btn_login);
        } else {
            field_login.clear();
            field_password.clear();
            clearPasswordField();
            showErrorMessage("Invalid login or password");
        }
    }

    static void setAuthenticationData(String login, String password) {
        AuthenticationController.login = login;
        AuthenticationController.password = password;
    }

    static void clearPasswordField() {
        password = "";
    }
}