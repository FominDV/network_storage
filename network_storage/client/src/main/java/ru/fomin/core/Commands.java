package ru.fomin.core;

import javafx.scene.control.Button;
import ru.fomin.gui.controllers.MainPanelController;

public interface Commands {
    String sendFile(String filename, String fileName);
    void exitToAuthentication(Button button);
    void getCurrentDirectoryEntity();
    boolean download(Long id, String path, String fileName);
    boolean delete(Long id, String type);
    String registration(String login, String password);
    void authentication(String login, String password);
    String getCurrentDirectory();
    String createDir(String dirName);
    void setMainPanelController(MainPanelController mainPanelController);
}
