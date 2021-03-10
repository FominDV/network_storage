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
    private TextField field_directory_name;

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
    private Button btn_out_dir;

    @FXML
    private Button btn_into_dir;

    @FXML
    private Label label_current_dir;

    @FXML
    void initialize() {
        commands = HandlerCommands.getCommands();

        updateFileList();

        updateCurrentDirectory();

        btn_info.setOnAction(event -> showDeveloperInfo());

        btn_exit.setOnAction(event -> commands.exitToAuthentication(btn_info));

        btn_upload.setOnAction(event -> upload());

        btn_download.setOnAction(event -> download());

        btn_delete.setOnAction(event -> delete());

        btn_change_password.setOnAction(event -> showAndHideStages("/fxml/update_password.fxml", btn_change_password));

        btn_create_dir.setOnAction(event -> createDirectory());
    }

    private void createDirectory() {
        String dirName=field_directory_name.getText();
        if(!hasText(dirName)){
            showErrorMessage("You should insert name of new directory into the filed");
            field_directory_name.setText("");
            return;
        }
        if (dirName.contains(" ")||dirName.contains("\\")){
            showErrorMessage("Name of directory should not contain spaces and '\\'");
            field_directory_name.setText("");
            return;
        }

        switch (commands.createDir(dirName)){
            case KeyCommands.DONE:
                updateFileList();
                showInfoMessage(dirName+" was created");
                break;
            case KeyCommands.ALREADY_EXIST:
                showErrorMessage(String.format("The directory %s already exist",dirName));
                break;
            default:
                showConnectionError();
        }
        field_directory_name.setText("");
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
            return;
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
        String fileName = file.getName();
        if (fileName.contains(" ")) {
            if (isConfirmChangeName(fileName)) {
                fileName = fileName.replace(" ", "_");
            } else {
                return;
            }
        }
        if (!(feedback = commands.sendFile(file.toString(), fileName)).equals(KeyCommands.DONE)) {
            showErrorMessage(feedback);
            return;
        }
        updateFileList();
        showInfoMessage(String.format("Upload %s is successful", fileName));
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

    private void updateCurrentDirectory() {
        String currentDirectory;
        if (!(currentDirectory = commands.getCurrentDirectory()).equals(KeyCommands.ERROR)) {
            label_current_dir.setText(currentDirectory);
        }
    }
}
