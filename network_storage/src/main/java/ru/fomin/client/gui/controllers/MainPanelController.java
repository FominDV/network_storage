package ru.fomin.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class MainPanelController {

    @FXML
    private Label title;

    @FXML
    private Button btn_info;

    @FXML
    private Button btn_cancel;

    @FXML
    private TextField field_file_path;

    @FXML
    private Button btn_change_password;

    @FXML
    private Button btn_upload;

    @FXML
    private Button btn_delete;

    @FXML
    private Button btn_save;

    @FXML
    private ListView<?> list_files;

    @FXML
    private Button btn_create_dir;

    @FXML
    void initialize() {

    }
}
