package ru.fomin.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import ru.fomin.core.Commands;
import ru.fomin.core.HandlerCommands;
import ru.fomin.KeyCommands;

import static ru.fomin.util.ControllersUtil.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainPanelController {

    private Commands commands;
    private Map<String, Long> fileMap = new HashMap<>();
    private Map<String, Long> directoryMap = new HashMap<>();
    private ObservableList<String> observableList;
    private MultipleSelectionModel<String> multipleSelectionModel;
    private FileChooser fileChooser = new FileChooser();
    private DirectoryChooser directoryChooser = new DirectoryChooser();

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

        updateFileList();

        btn_info.setOnAction(event -> showDeveloperInfo());

        btn_exit.setOnAction(event -> commands.exitToAuthentication(btn_info));

        btn_upload.setOnAction(event -> upload());

        btn_download.setOnAction(event -> download());

        btn_delete.setOnAction(event -> delete());

        btn_change_password.setOnAction(event -> showAndHideStages("/fxml/update_password.fxml", btn_change_password));
    }

    private void delete() {
        String fileName = multipleSelectionModel.getSelectedItem();
        if (!hasText(fileName)) {
            showErrorMessage("File was not chosen");
            return;
        }
        Long id;
        String type;
        if (fileMap.containsKey(fileName)) {
            type = KeyCommands.DELETE_FILE;
            id = fileMap.get(fileName);
        } else {
            type = KeyCommands.DELETE_DIR;
            id = directoryMap.get(fileName);
        }
        if (commands.delete(id, type)) {
            observableList.remove(fileName);
            if (type.equals(KeyCommands.DELETE_FILE)) {
                fileMap.remove(fileName);
            } else {
                directoryMap.remove(fileName);
            }
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
        if (!fileMap.containsKey(fileName)) {
            showErrorMessage("It is the directory\nYou can download only file\nChoose the file, please");
        }
        File directory = directoryChooser.showDialog(null);
        if (!directory.isDirectory()) {
            showErrorMessage("It is not valid directory");
            return;
        }
        Long id = fileMap.get(fileName);
        if (commands.download(id, directory.toString(), fileName)) {
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
        updateFileList();
        field_file_path.setText("");
        showInfoMessage(String.format("Upload %s is successful", file.getName()));
    }

    private void updateFileList() {
        fileMap.clear();
        directoryMap.clear();
        boolean isFile = true;
        String[] filesData = commands.getFiles();
        List<String> filesList = new ArrayList<>(filesData.length / 2);
        for (int i = 0; i < filesData.length; i++) {
            if (filesData[i].equals(KeyCommands.HARD_DELIMITER)) {
                isFile = false;
                continue;
            }
            filesList.add(filesData[i]);
            if (isFile) {
                fileMap.put(filesData[i], Long.parseLong(filesData[++i]));
            } else {
                directoryMap.put(filesData[i], Long.parseLong(filesData[++i]));
            }
        }
        observableList = FXCollections.observableArrayList(filesList);
        list_files.setItems(observableList);
        multipleSelectionModel = list_files.getSelectionModel();
        if (multipleSelectionModel.getSelectedItem() == null) multipleSelectionModel.select(0);
    }
}
