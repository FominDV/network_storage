package ru.fomin.client.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import ru.fomin.client.core.Commands;
import ru.fomin.client.core.HandlerCommands;
import ru.fomin.common.KeyCommands;

import java.io.File;

import static ru.fomin.client.util.ControllersUtil.*;

public class MainPanelController {

    private Commands commands;
    private ObservableList<String> observableList;
    private MultipleSelectionModel<String> multipleSelectionModel;
    private FileChooser fileChooser = new FileChooser();
    private DirectoryChooser directoryChooser=new DirectoryChooser();

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
    private ListView<String> list_files;

    @FXML
    private Button btn_create_dir;

    @FXML
    void initialize() {
        commands = HandlerCommands.getCommands();

        updateFileList(commands.getFiles());

        btn_info.setOnAction(event -> showDeveloperInfo());

        btn_exit.setOnAction(event -> commands.exitToAuthentication(btn_info));

        btn_upload.setOnAction(event -> upload());

        btn_download.setOnAction(event -> download());

        btn_delete.setOnAction(event -> delete());
    }

    private void delete() {
        String fileName = multipleSelectionModel.getSelectedItem();
        if (!hasText(fileName)) {
            showErrorMessage("File was not chosen");
            return;
        }
        if (commands.delete(fileName)) {
            observableList.remove(fileName);
            showInfoMessage(String.format("\"%s\" was deleted", fileName));
        } else {
            showErrorMessage("ERROR of connection or server\nPlease, try again");
        }
    }

    private void download() {
        String fileName = multipleSelectionModel.getSelectedItem();
        if (!hasText(fileName)) {
            showErrorMessage("File was not chosen");
            return;
        }
        File directory = directoryChooser.showDialog(null);
        if (!directory.isDirectory()) {
            showErrorMessage("Wrong directory");
            return;
        }
        if (commands.download(fileName, directory.toString())) {
            showInfoMessage(String.format("Download %s is successful", fileName));
        }
    }

    private void upload() {
        File file = fileChooser.showOpenDialog(null);
        String feedback;
        if (!file.isFile()) {
            showErrorMessage(String.format("Path \"%s\" is not file", file.toString()));
            return;
        }
        if (!(feedback = commands.sendFile(file.toString())).equals(KeyCommands.DONE)) {
            showErrorMessage(feedback);
            return;
        }
        observableList.add(file.getName());
        field_file_path.setText("");
        showInfoMessage(String.format("Upload %s is successful", file.getName()));
    }

    private void updateFileList(String[] filesArray) {
        observableList = FXCollections.observableArrayList(filesArray);
        list_files.setItems(observableList);
        multipleSelectionModel = list_files.getSelectionModel();
        if (multipleSelectionModel.getSelectedItem() == null) multipleSelectionModel.select(0);
    }
}
