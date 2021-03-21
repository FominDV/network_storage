package ru.fomin.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import ru.fomin.util.ControllersUtil;

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

        btn_info.setOnAction(event -> ControllersUtil.showDeveloperInfo());

        btn_cancel.setOnAction(event -> ControllersUtil.showAndHideStages("/fxml/main_panel.fxml", btn_cancel));
    }
}
