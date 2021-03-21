package ru.fomin.core;

import javafx.scene.control.Button;
import ru.fomin.gui.controllers.MainPanelController;
import ru.fomin.gui.controllers.RegistrationController;
import ru.fomin.gui.controllers.UpdatePasswordController;
import ru.fomin.need.commands.CreatingAndUpdatingManipulationCommand;
import ru.fomin.need.commands.FileManipulationRequest;

import java.io.File;

public interface Commands {
    void sendFile(File file, Long directoryId);
    void exitToAuthentication(Button button);
    void getCurrentDirectoryEntity();
    void download(Long id, String path);
    void delete(Long id, FileManipulationRequest.Request type);
    void registration(String login, String password);
    void authentication(String login, String password);
    void createDir(String dirName, Long remoteDirectoryId);
    void rename(String dirName, Long remoteDirectoryId, CreatingAndUpdatingManipulationCommand.Type type);
    void setMainPanelController(MainPanelController mainPanelController);
    void setRegistrationController(RegistrationController registrationController);
    void setUpdatePasswordController(UpdatePasswordController updatePasswordController);

}
