package ru.fomin.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.PasswordField;
import ru.fomin.core.Commands;
import ru.fomin.core.MainHandler;
import ru.fomin.util.ControllersUtil;

public class UpdatePasswordController {

    private Commands commands;

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
        commands = MainHandler.getCommands();

        btn_info.setOnAction(event -> ControllersUtil.showDeveloperInfo());

        btn_cancel.setOnAction(event -> ControllersUtil.showAndHideStages("/fxml/main_panel.fxml", btn_cancel));

        MainHandler.setUpdatePasswordController(this);
    }

    public Labeled getLabeled() {
        return btn_info;
    }
}
