package ru.fomin.core.handlers;

import javafx.application.Platform;
import ru.fomin.commands.AuthResult;
import ru.fomin.commands.CurrentDirectoryEntityList;
import ru.fomin.commands.FileManipulationResponse;
import ru.fomin.gui.controllers.AuthenticationController;
import ru.fomin.gui.controllers.MainPanelController;
import ru.fomin.gui.controllers.RegistrationController;
import ru.fomin.gui.controllers.UpdatePasswordController;

import static ru.fomin.util.ControllersUtil.*;

public class ResponseHandler {

    private static ResponseHandler instance;
    private static AuthenticationController authenticationController;
    private static MainPanelController mainPanelController;
    private static RegistrationController registrationController;
    private static UpdatePasswordController updatePasswordController;

    private ResponseHandler(){}

    public static ResponseHandler getInstance(){
        if (instance==null){
            instance=new ResponseHandler();
        }
        return instance;
    }

    public static void exitOnFatalConnectionError() {
        if (mainPanelController != null) {
            Platform.runLater(() -> hideWindow(mainPanelController.getLabeled()));
        }
        if (registrationController != null) {
            Platform.runLater(() -> hideWindow(registrationController.getLabeled()));
        }
        if (updatePasswordController != null) {
            Platform.runLater(() -> hideWindow(updatePasswordController.getLabeled()));
        }
        Platform.runLater(() -> {
            showStage("/fxml/authentication.fxml");
            showErrorMessage("Connection was lost");
        });
    }

    public void handleResponse(AuthResult authResult) {
        AuthResult.Result result = authResult.getResult();
        if (result == AuthResult.Result.FAIL_AUTH || result == AuthResult.Result.OK_AUTH) {
            authenticationController.handleResponse(result);
        } else {
            registrationController.handleResponse(result, authResult.getLogin());
        }
    }

    public void updateDirectoryEntity(CurrentDirectoryEntityList com) {
        mainPanelController.updateDirectoryEntity(com);
    }

    public void getFileManipulationResponse(FileManipulationResponse response) {
        mainPanelController.getFileManipulationResponse(response);
    }

    public void downloadingSuccessful(String filename) {
        showInfoMessage(String.format("Downloading file \"%s\" is successfully", filename));
    }

    public void downloadingError(String fileName) {
        showErrorMessage(String.format("Downloading file \"%s\" is failed", fileName));
    }

    public static void setMainPanelController(MainPanelController mainPanelController) {
        ResponseHandler.mainPanelController = mainPanelController;
    }

    public static void setRegistrationController(RegistrationController registrationController) {
        ResponseHandler.registrationController = registrationController;
    }

    public static void setUpdatePasswordController(UpdatePasswordController updatePasswordController) {
        ResponseHandler.updatePasswordController = updatePasswordController;
    }

    public static void setAuthenticationController(AuthenticationController authenticationController) {
        ResponseHandler.authenticationController = authenticationController;
    }
}
