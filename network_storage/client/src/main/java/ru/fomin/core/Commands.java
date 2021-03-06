package ru.fomin.core;

import javafx.scene.control.Button;

public interface Commands {
    String sendFile(String filename);
    void exitToAuthentication(Button button);
    String[] getFiles();
    boolean download(String filename, String path);
    boolean delete(String filename);
    String registration(String login, String password);
    boolean authentication(String login, String password);
}
