package ru.fomin.core;

import javafx.scene.control.Button;

public interface Commands {
    String sendFile(String filename, String fileName);
    void exitToAuthentication(Button button);
    String[] getFiles();
    boolean download(Long id, String path, String fileName);
    boolean delete(Long id, String type);
    String registration(String login, String password);
    void authentication(String login, String password);
    String getCurrentDirectory();
    String createDir(String dirName);
}
