package ru.fomin.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import lombok.Getter;
import ru.fomin.util.encoder.Encoder;
import ru.fomin.factory.Factory;
import ru.fomin.services.ChangingPasswordService;
import ru.fomin.services.impl.ResponseService;
import ru.fomin.util.ControllersUtil;

/**
 * Window for changing password.
 */
public class UpdatePasswordController {

    private static final Encoder encoder = Factory.getEncoder();

    private ChangingPasswordService changingPasswordService;

    @FXML
    @Getter
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
        changingPasswordService = Factory.getChangingPasswordRequest();

        btn_info.setOnAction(event -> ControllersUtil.showDeveloperInfo());

        btn_cancel.setOnAction(event -> ControllersUtil.showAndHideStages("/fxml/main_panel.fxml", btn_cancel));

        btn_change.setOnAction(event -> changePassword());

        ResponseService.setUpdatePasswordController(this);
    }

    private void changePassword() {

        //captures values
        String currentPassword = field_password.getText();
        String newPassword = field_new_password.getText();
        String repeatedPassword = field_repeat_password.getText();

        //clears fields
        field_password.clear();
        field_new_password.clear();
        field_repeat_password.clear();

        //checks the password values for correctness
        if (ControllersUtil.verifyEmptyField(currentPassword, newPassword, repeatedPassword) && ControllersUtil.verifyPassword(newPassword, repeatedPassword)) {

            //Caching password
            currentPassword = encoder.encode(currentPassword);
            newPassword = encoder.encode(newPassword);

            changingPasswordService.changePassword(currentPassword, newPassword);
        }
    }

    public void handleResponse(boolean isSuccessful) {
        AuthenticationController.clearPasswordField();
        if (isSuccessful) {
            ControllersUtil.showInfoMessage("Your password was changed successfully.");
            ControllersUtil.showAndHideStages("/fxml/main_panel.fxml", btn_cancel);
        } else {
            ControllersUtil.showErrorMessage("<html>Incorrect current password.<br>Please, try again.</html>");
        }
    }
}
