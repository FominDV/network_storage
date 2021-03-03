package ru.fomin.client.core;

import javafx.application.Application;
import javafx.scene.control.Button;

public interface Commands {
    String sendFile(String filename);
    void exitToAuthentication(Button button);
    String[] getFiles();
}
