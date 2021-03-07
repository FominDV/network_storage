package ru.fomin.core;

import javafx.scene.control.Button;
import ru.fomin.KeyCommands;

public interface Commands {
    String sendFile(String filename);
    void exitToAuthentication(Button button);
    String[] getFiles();
    boolean download(Long id, String path, String fileName);
    boolean delete(Long id, String type);
    String registration(String login, String password);
    boolean authentication(String login, String password);
}
