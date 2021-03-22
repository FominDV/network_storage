package ru.fomin.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import ru.fomin.core.Commands;
import ru.fomin.core.MainHandler;
import ru.fomin.classes.Constants;
import ru.fomin.commands.CreatingAndUpdatingManipulationRequest;
import ru.fomin.commands.CurrentDirectoryEntityList;
import ru.fomin.commands.FileManipulationRequest;
import ru.fomin.commands.FileManipulationResponse;

import static ru.fomin.commands.FileManipulationRequest.Request.DELETE_DIR;
import static ru.fomin.commands.FileManipulationRequest.Request.DELETE_FILE;
import static ru.fomin.util.ControllersUtil.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MainPanelController {

    private Commands commands;
    private Map<String, Long> fileMap = new HashMap<>();
    private Map<String, Long> directoryMap = new HashMap<>();
    private ObservableList<String> observableList;
    private MultipleSelectionModel<String> multipleSelectionModel;
    private FileChooser fileChooser = new FileChooser();
    private DirectoryChooser directoryChooser = new DirectoryChooser();
    private Long remoteDirectoryId;

    @FXML
    private Label title;

    @FXML
    private Button btn_info;

    @FXML
    private Button btn_exit;

    @FXML
    private TextField field_resource_name;

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
    private Button btn_rename;

    @FXML
    void initialize() {
        commands = MainHandler.getCommands();

        btn_info.setOnAction(event -> showDeveloperInfo());

        btn_exit.setOnAction(event -> commands.exitToAuthentication(btn_info));

        btn_upload.setOnAction(event -> upload());

        btn_download.setOnAction(event -> download());

        btn_delete.setOnAction(event -> delete());

        btn_change_password.setOnAction(event -> showAndHideStages("/fxml/update_password.fxml", btn_change_password));

        btn_create_dir.setOnAction(event -> createDirectory());

        btn_rename.setOnAction(event -> rename());

        MainHandler.setMainPanelController(this);
        commands.getCurrentDirectoryEntity();
    }

    private void rename() {
        String resourceName = multipleSelectionModel.getSelectedItem();
        if (!hasText(resourceName)) {
            showErrorMessage("Resource for renaming was not chosen");
            return;
        }
        String newName = field_resource_name.getText();
        if (!hasText(newName)) {
            showErrorMessage("You should insert new name of resource into the filed");
            field_resource_name.setText("");
            return;
        }

        Long id;
        CreatingAndUpdatingManipulationRequest.Type type;
        //Searching resource
        if (fileMap.containsKey(resourceName)) {
            //Verify duplicate names
            if (fileMap.containsKey(Constants.getFileNamePrefix() + newName)) {
                showErrorMessage(String.format("File with the name \"%s\" already exist", newName));
                field_resource_name.setText("");
                return;
            }
            type = CreatingAndUpdatingManipulationRequest.Type.RENAME_FILE;
            id = fileMap.get(resourceName);
        } else if (directoryMap.containsKey(resourceName)) {
            //Verify duplicate names
            if (directoryMap.containsKey(Constants.getDirectoryNamePrefix() + newName)) {
                showErrorMessage(String.format("Directory with the name \"%s\" already exist", newName));
                field_resource_name.setText("");
                return;
            }
            type = CreatingAndUpdatingManipulationRequest.Type.RENAME_DIR;
            id = directoryMap.get(resourceName);
        } else {
            showErrorMessage("Fatal error");
            commands.exitToAuthentication(btn_info);
            return;
        }

        commands.rename(newName, id, type);
        field_resource_name.setText("");
    }

    private void createDirectory() {
        String dirName = field_resource_name.getText();
        if (!hasText(dirName)) {
            showErrorMessage("You should insert name of new directory into the filed");
            field_resource_name.setText("");
            return;
        }
        commands.createDir(dirName, remoteDirectoryId);
        field_resource_name.setText("");
    }

    private void delete() {
        String fileName = multipleSelectionModel.getSelectedItem();
        if (!hasText(fileName)) {
            showErrorMessage("File was not chosen");
            return;
        }
        Long id;
        FileManipulationRequest.Request type;
        if (fileMap.containsKey(fileName)) {
            type = DELETE_FILE;
            id = fileMap.get(fileName);
        } else if (directoryMap.containsKey(fileName)) {
            if (!isConfirmDeleteDirectory()) {
                return;
            }
            type = DELETE_DIR;
            id = directoryMap.get(fileName);
        } else {
            showErrorMessage("Fatal error");
            commands.exitToAuthentication(btn_info);
            return;
        }
        commands.delete(id, type);
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
        if (directory == null) {
            return;
        }
        if (!directory.isDirectory()) {
            showErrorMessage("It is not valid directory");
            return;
        }
        String realFileName = fileName.substring(Constants.getFileNamePrefix().length());
        if (Files.exists(Paths.get(directory.toString(), realFileName)) && !isConfirmOverrideFile(realFileName)) {
            return;
        }
        Long id = fileMap.get(fileName);
        commands.download(id, directory.toString());
    }

    private void upload() {
        File file = fileChooser.showOpenDialog(null);
        if (file == null) {
            return;
        }
        if (!file.isFile()) {
            showErrorMessage(String.format("Path \"%s\" is not file", file.toString()));
            return;
        }
        commands.sendFile(file, remoteDirectoryId);
        commands.getCurrentDirectoryEntity();
    }


    public void updateDirectoryEntity(CurrentDirectoryEntityList com) {
        fileMap = com.getFileMap();
        directoryMap = com.getDirectoryMap();
        label_current_dir.setText(com.getCurrentDirectory());
        remoteDirectoryId = com.getCurrentDirectoryId();

        //Creating list of all files and nested directories
        List<String> filesList = new ArrayList<>();
        filesList.addAll(fileMap.keySet());
        filesList.addAll(directoryMap.keySet());

        observableList = FXCollections.observableArrayList(filesList);
        list_files.setItems(observableList);
        multipleSelectionModel = list_files.getSelectionModel();
        if (multipleSelectionModel.getSelectedItem() == null) multipleSelectionModel.select(0);
    }

    public synchronized void getFileManipulationResponse(FileManipulationResponse response) {
        String fileName = response.getFileName();
        Long id = response.getId();
        switch (response.getResponse()) {
            case DIR_ALREADY_EXIST:
                showErrorMessage(String.format("Directory with the name \"%s\" already exist", fileName));
                break;
            case FILE_ALREADY_EXIST:
                showErrorMessage(String.format("File with the name \"%s\" already exist", fileName));
                break;
            case FILE_UPLOADED:
                String fileSpecialName = Constants.getFileNamePrefix() + fileName;
                fileMap.put(fileSpecialName, id);
                observableList.add(fileSpecialName);
                showInfoMessage(String.format("Uploading of file \"%s\" is successful", fileName));
                break;
            case DIR_CREATED:
                String directorySpecialName = Constants.getDirectoryNamePrefix() + fileName;
                directoryMap.put(directorySpecialName, id);
                observableList.add(directorySpecialName);
                showInfoMessage(String.format("The directory \"%s\" was created", fileName));
                break;
            case FILE_REMOVED:
                for (Map.Entry<String, Long> entry : fileMap.entrySet()) {
                    if (entry.getValue().equals(id)) {
                        String removingFileName = entry.getKey();
                        fileMap.remove(removingFileName);
                        observableList.remove(removingFileName);
                        break;
                    }
                }
                showInfoMessage(String.format("The file \"%s\" was removed", fileName));
                break;
            case DIRECTORY_REMOVED:
                for (Map.Entry<String, Long> entry : directoryMap.entrySet()) {
                    if (entry.getValue().equals(id)) {
                        String removingFileName = entry.getKey();
                        directoryMap.remove(removingFileName);
                        observableList.remove(removingFileName);
                        break;
                    }
                }
                showInfoMessage(String.format("The directory \"%s\" with all that it contains was removed", fileName));
                break;
            case RENAME_DIR:
                handleRenameResponse(directoryMap, "directory", id, fileName, Constants.getDirectoryNamePrefix());
                break;
            case RENAME_FILE:
                handleRenameResponse(fileMap, "file", id, fileName, Constants.getFileNamePrefix());
                break;
            default:
                showErrorMessage(String.format("Unknown response \"%s\" from server", response.getResponse()));
                commands.exitToAuthentication(btn_info);
        }
    }

    private void handleRenameResponse(Map<String, Long> map, String resource, Long id, String newName, String prefix) {
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            if (entry.getValue().equals(id)) {
                String oldName = entry.getKey();
                String newSpecialName = prefix + newName;
                map.remove(oldName);
                map.put(newSpecialName, id);
                observableList.remove(oldName);
                observableList.add(newSpecialName);
                showInfoMessage(String.format("The %s \"%s\" was renamed to \"%s\"", resource, oldName, newName));
                break;
            }
        }
    }

    public Labeled getLabeled() {
        return btn_info;
    }
}
