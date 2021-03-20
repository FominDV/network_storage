package ru.fomin.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.fomin.core.Commands;
import ru.fomin.core.HandlerCommands;
import ru.fomin.KeyCommands;
import ru.fomin.util.ControllersUtil;

public class RegistrationController {

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

        btn_info.setOnAction(event -> ControllersUtil.showDeveloperInfo());

        btn_cancel.setOnAction(event -> commands.exitToAuthentication(btn_cancel));

        btn_registration.setOnAction(event -> registration());
    }

    private void registration() {
        String login = field_login.getText();
        String password = field_password.getText();
        if (!ControllersUtil.validation(login, password, field_repeat_password.getText())) {
            clearFields();
            return;
        }
    }

    private void clearFields() {
        field_login.setText("");
        field_password.setText("");
        field_repeat_password.setText("");
    }
}

