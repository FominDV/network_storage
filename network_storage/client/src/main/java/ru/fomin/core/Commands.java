package ru.fomin.core;

import javafx.scene.control.Button;
import ru.fomin.gui.controllers.MainPanelController;

import java.io.File;

public interface Commands {
    void sendFile(File file, Long directoryId);
    void exitToAuthentication(Button button);
    void getCurrentDirectoryEntity();
    void download(Long id, String path);
    boolean delete(Long id, String type);
    String registration(String login, String password);
    void authentication(String login, String password);
    String createDir(String dirName);
    void setMainPanelController(MainPanelController mainPanelController);
}
