package ru.fomin.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.fomin.enumeration.AuthAndRegResult;
import ru.fomin.util.encoder.Encoder;
import ru.fomin.factory.Factory;
import ru.fomin.service.RegistrationService;
import ru.fomin.service.impl.ResponseService;
import ru.fomin.util.ControllersUtil;

/**
 * Window for creating new account.
 */
public class RegistrationController {

    private static final Encoder encoder = Factory.getEncoder();

    private boolean isWaitingResponse;
    private String currentPassword = "";
    private RegistrationService registrationRequest;

    @FXML
    private Button btn_info;

    @FXML
    private Button btn_cancel;

    @FXML
    private Button btn_registration;

    @FXML
    private TextField field_login;

    @FXML
    private PasswordField field_password;

    @FXML
    private PasswordField field_repeat_password;

    @FXML
    void initialize() {
        registrationRequest = Factory.getRegistrationRequest();

        btn_info.setOnAction(event -> ControllersUtil.showDeveloperInfo());

        btn_cancel.setOnAction(event -> registrationRequest.exitToAuthentication(btn_cancel));

        btn_registration.setOnAction(event -> registration());

        ResponseService.setRegistrationController(this);
    }

    private void registration() {
        String login = field_login.getText();
        String password = field_password.getText();
        if (!isWaitingResponse && ControllersUtil.validation(login, password, field_repeat_password.getText())) {
            isWaitingResponse = true;
            currentPassword = password;
            registrationRequest.registration(login, encoder.encode(password));
        } else {
            clearFields();
        }

    }

    private void clearFields() {
        field_login.clear();
        field_password.clear();
        field_repeat_password.clear();
    }

    /**
     * Handling response from server.
     */
    public void handleResponse(AuthAndRegResult authAndRegResult, String login) {
        if (authAndRegResult == AuthAndRegResult.OK_REG) {
            AuthenticationController.setAuthenticationData(login, currentPassword);
            ControllersUtil.showInfoMessage(String.format("Registration is successful\nYour login is \"%s\"", login));
            registrationRequest.exitToAuthentication(btn_info);
        } else {
            ControllersUtil.showErrorMessage(String.format("Registration is failed\nLogin \"%s\" already exist", login));
            isWaitingResponse = false;
        }
    }

    public Labeled getLabeled() {
        return btn_info;
    }
}

