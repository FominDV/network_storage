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
import ru.fomin.util.ControllersUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainPanelController {

    private Commands commands;
    private Map<String, Long> fileMap;
    private Map<String,Long> directoryMap;
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

        updateFileList();

        btn_info.setOnAction(event -> ControllersUtil.showDeveloperInfo());

        btn_exit.setOnAction(event -> commands.exitToAuthentication(btn_info));

        btn_upload.setOnAction(event -> upload());

        btn_download.setOnAction(event -> download());

        btn_delete.setOnAction(event -> delete());

        btn_change_password.setOnAction(event -> ControllersUtil.showAndHideStages("/fxml/update_password.fxml",btn_change_password));
    }

    private void delete() {
        String fileName = multipleSelectionModel.getSelectedItem();
        if (!ControllersUtil.hasText(fileName)) {
            ControllersUtil.showErrorMessage("File was not chosen");
            return;
        }
        if (commands.delete(fileName)) {
            observableList.remove(fileName);
            ControllersUtil.showInfoMessage(String.format("\"%s\" was deleted", fileName));
        } else {
            ControllersUtil.showErrorMessage("ERROR of connection or server\nPlease, try again");
        }
    }

    private void download() {
        String fileName = multipleSelectionModel.getSelectedItem();
        if (!ControllersUtil.hasText(fileName)) {
            ControllersUtil.showErrorMessage("File was not chosen");
            return;
        }
        File directory = directoryChooser.showDialog(null);
        if (!directory.isDirectory()) {
            ControllersUtil.showErrorMessage("Wrong directory");
            return;
        }
        if (commands.download(fileName, directory.toString())) {
            ControllersUtil.showInfoMessage(String.format("Download %s is successful", fileName));
        }
    }

    private void upload() {
        File file = fileChooser.showOpenDialog(null);
        String feedback;
        if (!file.isFile()) {
            ControllersUtil.showErrorMessage(String.format("Path \"%s\" is not file", file.toString()));
            return;
        }
        if (!(feedback = commands.sendFile(file.toString())).equals(KeyCommands.DONE)) {
            ControllersUtil.showErrorMessage(feedback);
            return;
        }
        observableList.add(file.getName());
        field_file_path.setText("");
        ControllersUtil.showInfoMessage(String.format("Upload %s is successful", file.getName()));
    }

    private void updateFileList() {
        boolean isFile=true;
        String[] filesData = commands.getFiles();
        List<String> filesList = new ArrayList<>(filesData.length/2);
        for (int i = 0; i < filesData.length; i++) {
            if(filesData[i+1].equals(KeyCommands.HARD_DELIMITER)){
                isFile=false;
            }
            filesList.add(filesData[i]);
            if(isFile){
            fileMap.put(filesData[i],Long.parseLong(filesData[i++]));}else {
                directoryMap.put(filesData[i],Long.parseLong(filesData[i++]));
            }
        }
        observableList = FXCollections.observableArrayList(filesList);
        list_files.setItems(observableList);
        multipleSelectionModel = list_files.getSelectionModel();
        if (multipleSelectionModel.getSelectedItem() == null) multipleSelectionModel.select(0);
    }
}
