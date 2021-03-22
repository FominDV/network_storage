package ru.fomin.core;

import javafx.scene.control.Button;
import ru.fomin.gui.controllers.AuthenticationController;
import ru.fomin.gui.controllers.MainPanelController;
import ru.fomin.gui.controllers.RegistrationController;
import ru.fomin.gui.controllers.UpdatePasswordController;
import ru.fomin.commands.CreatingAndUpdatingManipulationRequest;
import ru.fomin.commands.FileManipulationRequest;

import java.io.File;
import java.io.IOException;

public interface Commands {
    void sendFile(File file, Long directoryId);
    void exitToAuthentication(Button button);
    void getCurrentDirectoryEntity();
    void download(Long id, String path);
    void delete(Long id, FileManipulationRequest.Request type);
    void registration(String login, String password);
    void authentication(String login, String password);
    void createDir(String dirName, Long remoteDirectoryId);
    void rename(String dirName, Long remoteDirectoryId, CreatingAndUpdatingManipulationRequest.Type type);
    void connect() throws IOException;
}
