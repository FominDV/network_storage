package ru.fomin.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.fomin.client.core.Commands;
import ru.fomin.client.core.HandlerCommands;
import ru.fomin.common.KeyCommands;

import static ru.fomin.client.util.ControllersUtil.*;

public class RegistrationController {

    private static final String PASSWORD_PATTERN = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9@#$%]).{8,}";

    private Commands commands;

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
        commands = HandlerCommands.getCommands();

        btn_info.setOnAction(event -> showDeveloperInfo());

        btn_cancel.setOnAction(event -> commands.exitToAuthentication(btn_cancel));

        btn_registration.setOnAction(event -> registration());
    }

    private void registration() {
        String login = field_login.getText();
        String password = field_password.getText();
        if (validation(login, password)) {
            switch (commands.registration(login, password)) {
                case KeyCommands.DONE:
                    showInfoMessage("Registration is successful\nYour login: " + login);
                    AuthenticationController.setAuthenticationData(login,password);
                    commands.exitToAuthentication(btn_cancel);
                    break;
                case KeyCommands.DUPLICATED_LOGIN:
                    field_login.setText("");
                    clearPasswordFields();
                    showErrorMessage(String.format("Login %s already exist", login));
                    break;
                default:
                    showErrorMessage("ERROR of server");
            }
        }
    }

    private boolean validation(String login, String password) {
        String repeated_password = field_repeat_password.getText();

        //Verify empty field
        if (!(hasText(login) || hasText(password) || hasText(repeated_password))) {
            showErrorMessage("All field should be fill");
            return false;
        }

        //Verify length of login
        int loginLends = login.length();
        if (loginLends < 3 || loginLends > 20) {
            field_login.setText("");
            clearPasswordFields();
            showErrorMessage("Length of login should be greater than 2 and less than 21");
            return false;
        }

        //Verify password entity
        if (!password.matches(PASSWORD_PATTERN)) {
            showErrorMessage("The password should be at least 8 characters long,\n contain at least 1 large letter,\n one small letter\n and contain special character OR digit.");
            clearPasswordFields();
            return false;
        }

        //Verify password comparison
        if (!password.equals(repeated_password)) {
            showErrorMessage("The passwords don't match");
            clearPasswordFields();
            return false;
        }
        return true;
    }

    private void clearPasswordFields() {
        field_password.setText("");
        field_repeat_password.setText("");
    }
}

