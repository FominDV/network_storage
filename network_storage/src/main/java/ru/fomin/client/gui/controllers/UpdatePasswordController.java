package ru.fomin.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

import static ru.fomin.client.util.ControllersUtil.showAndHideStages;
import static ru.fomin.client.util.ControllersUtil.showDeveloperInfo;

public class UpdatePasswordController {
    @FXML
    private Button btn_info;

    @FXML
    private Button btn_cancel;

    @FXML
    private Button btn_change;

    @FXML
    private PasswordField field_password;

    @FXML
    private PasswordField field_new_password;

    @FXML
    private PasswordField field_repeat_password;

    @FXML
    void initialize() {

        btn_info.setOnAction(event -> showDeveloperInfo());

        btn_cancel.setOnAction(event -> showAndHideStages("/fxml/main_panel.fxml", btn_cancel));


    }
}
