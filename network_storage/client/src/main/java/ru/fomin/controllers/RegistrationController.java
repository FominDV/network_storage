package ru.fomin.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.fomin.services.ResponseService;
import ru.fomin.services.RequestService;
import ru.fomin.dto.responses.AuthResult;
import ru.fomin.util.ControllersUtil;

/**
 * Window for creating new account.
 */
public class RegistrationController {

    private boolean isWaitingResponse;
    private String currentPassword = "";
    private RequestService requestService;

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
        requestService = RequestService.getInstance();

        btn_info.setOnAction(event -> ControllersUtil.showDeveloperInfo());

        btn_cancel.setOnAction(event -> requestService.exitToAuthentication(btn_cancel));

        btn_registration.setOnAction(event -> registration());

        ResponseService.setRegistrationController(this);
    }

    private void registration() {
        String login = field_login.getText();
        String password = field_password.getText();
        if (!isWaitingResponse && ControllersUtil.validation(login, password, field_repeat_password.getText())) {
            isWaitingResponse = true;
            currentPassword = password;
            requestService.registration(login, password);
        } else {
            clearFields();
        }

    }

    private void clearFields() {
        field_login.setText("");
        field_password.setText("");
        field_repeat_password.setText("");
    }

    /**
     * Handling response from server.
     */
    public void handleResponse(AuthResult.Result result, String login) {
        if (result == AuthResult.Result.OK_REG) {
            AuthenticationController.setAuthenticationData(login, currentPassword);
            ControllersUtil.showInfoMessage(String.format("Registration is successful\nYour login is \"%s\"", login));
            requestService.exitToAuthentication(btn_info);
        } else {
            ControllersUtil.showErrorMessage(String.format("Registration is failed\nLogin \"%s\" already exist", login));
            isWaitingResponse = false;
        }
    }

    public Labeled getLabeled() {
        return btn_info;
    }
}

