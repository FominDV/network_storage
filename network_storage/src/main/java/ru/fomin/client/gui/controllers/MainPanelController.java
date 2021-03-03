package ru.fomin.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import ru.fomin.client.core.Commands;
import ru.fomin.client.core.HandlerCommands;

import static ru.fomin.client.util.ControllersUtil.*;

public class MainPanelController {

    Commands commands;

    @FXML
    private Label title;

    @FXML
    private Button btn_info;

    @FXML
    private Button btn_exit;

    @FXML
    private TextField field_file_path;

    @FXML
    private Button btn_change_password;

    @FXML
    private Button btn_upload;

    @FXML
    private Button btn_delete;

    @FXML
    private Button btn_download;

    @FXML
    private ListView<?> list_files;

    @FXML
    private Button btn_create_dir;

    @FXML
    void initialize() {

        commands= HandlerCommands.getCommands();

        btn_info.setOnAction(event -> showDeveloperInfo());

        btn_exit.setOnAction(event -> commands.exitToAuthentication(btn_info));

        btn_upload.setOnAction(event -> commands.sendFile(field_file_path.getText()));
    }
}
