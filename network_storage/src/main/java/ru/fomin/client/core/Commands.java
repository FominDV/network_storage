package ru.fomin.client.core;

import javafx.application.Application;
import javafx.scene.control.Button;
import ru.fomin.common.KeyCommands;

public interface Commands {
    String sendFile(String filename);
    void exitToAuthentication(Button button);
    String[] getFiles();
    boolean download(String filename, String path);
    boolean delete(String filename);
    String registration(String login, String password);
}
