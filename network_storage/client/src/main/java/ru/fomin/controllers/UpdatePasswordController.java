package ru.fomin.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.PasswordField;
import ru.fomin.services.ResponseService;
import ru.fomin.services.RequestService;
import ru.fomin.util.ControllersUtil;

/**
 * Window for changing password.
 */
public class UpdatePasswordController {

    private RequestService requestService;

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
        requestService = RequestService.getInstance();

        btn_info.setOnAction(event -> ControllersUtil.showDeveloperInfo());

        btn_cancel.setOnAction(event -> ControllersUtil.showAndHideStages("/fxml/main_panel.fxml", btn_cancel));

        ResponseService.setUpdatePasswordController(this);
    }

    public Labeled getLabeled() {
        return btn_info;
    }
}