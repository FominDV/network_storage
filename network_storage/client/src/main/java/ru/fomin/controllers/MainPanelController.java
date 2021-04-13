package ru.fomin.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import ru.fomin.factory.Factory;
import ru.fomin.services.MainPanelService;
import ru.fomin.services.impl.ResponseService;
import ru.fomin.classes.Constants;
import ru.fomin.dto.requests.CreatingAndUpdatingManipulationRequest;
import ru.fomin.dto.responses.CurrentDirectoryEntityList;
import ru.fomin.dto.requests.FileManipulationRequest;
import ru.fomin.dto.responses.FileManipulationResponse;

import static ru.fomin.dto.requests.FileManipulationRequest.Request.DELETE_DIR;
import static ru.fomin.dto.requests.FileManipulationRequest.Request.DELETE_FILE;
import static ru.fomin.util.ControllersUtil.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Main work window for all operations with files.
 */
public class MainPanelController {

    /*key - name of resource on client side
      value - id of resource on server side*/
    private Map<String, Long> fileMap = new HashMap<>();
    private Map<String, Long> directoryMap = new HashMap<>();

    private MainPanelService mainPanelService;
    private ObservableList<String> observableList;
    private MultipleSelectionModel<String> multipleSelectionModel;
    private FileChooser fileChooser = new FileChooser();
    private DirectoryChooser directoryChooser = new DirectoryChooser();
    private Long remoteCurrentDirectoryId;
    private Long remoteRootDirectoryId;

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
        mainPanelService = Factory.getMainPanelRequest();

        btn_info.setOnAction(event -> showDeveloperInfo());

        btn_exit.setOnAction(event -> mainPanelService.exitToAuthentication(btn_info));

        btn_upload.setOnAction(event -> upload());

        btn_download.setOnAction(event -> download());

        btn_delete.setOnAction(event -> delete());

        btn_change_password.setOnAction(event -> showAndHideStages("/fxml/update_password.fxml", btn_change_password));

        btn_create_dir.setOnAction(event -> createDirectory());

        btn_rename.setOnAction(event -> rename());

        btn_into_dir.setOnAction(event -> moveToNestedDirectory());

        btn_out_dir.setOnAction(event -> moveFromCurrentDirectory());

        ResponseService.setMainPanelController(this);

        //Request to server for setting list of file and nested directories;
        mainPanelService.getCurrentDirectoryEntity();
    }

    private void moveFromCurrentDirectory() {
        if (remoteRootDirectoryId.equals(remoteCurrentDirectoryId)) {
            showErrorMessage("You are into root directory");
            return;
        }
        mainPanelService.moveFromCurrentDirectory(remoteCurrentDirectoryId);
    }

    private void moveToNestedDirectory() {
        Long id;
        if ((id = getIdOfChosenDirectory()) == null) {
            return;
        }
        mainPanelService.moveToNestedDirectory(id);
    }

    /**
     * Returns id of chosen directory from list.
     *
     * @return - null if directory was not chosen
     */
    private Long getIdOfChosenDirectory() {
        String resourceName = multipleSelectionModel.getSelectedItem();

        //Verify that something was chosen
        if (!hasText(resourceName)) {
            showErrorMessage("Directory was not chosen");
            return null;
        }

        //Verify that directory was chosen
        if (!directoryMap.containsKey(resourceName)) {
            showErrorMessage("You chose not directory");
            return null;
        }

        return directoryMap.get(resourceName);
    }

    /**
     * Rename chosen file or directory.
     */
    private void rename() {
        String resourceName = multipleSelectionModel.getSelectedItem();
        if (!hasText(resourceName)) {
            showErrorMessage("Resource for renaming was not chosen");
            return;
        }
        String newName = field_resource_name.getText();
        if (!hasText(newName)) {
            showErrorMessage("You should insert new name of resource into the filed");
            field_resource_name.clear();
            return;
        }

        Long id;
        CreatingAndUpdatingManipulationRequest.Type type;
        //Searching resource
        if (fileMap.containsKey(resourceName)) {
            //Verify duplicate names
            if (fileMap.containsKey(Constants.getFILE_NAME_PREFIX() + newName)) {
                showErrorMessage(String.format("File with the name \"%s\" already exist", newName));
                field_resource_name.clear();
                return;
            }
            type = CreatingAndUpdatingManipulationRequest.Type.RENAME_FILE;
            id = fileMap.get(resourceName);
        } else if (directoryMap.containsKey(resourceName)) {
            //Verify duplicate names
            if (directoryMap.containsKey(Constants.getDIRECTORY_NAME_PREFIX() + newName)) {
                showErrorMessage(String.format("Directory with the name \"%s\" already exist", newName));
                field_resource_name.clear();
                return;
            }
            type = CreatingAndUpdatingManipulationRequest.Type.RENAME_DIR;
            id = directoryMap.get(resourceName);
        } else {
            //resource is not contained into maps
            showErrorMessage("Fatal error");
            mainPanelService.exitToAuthentication(btn_info);
            return;
        }

        mainPanelService.rename(newName, id, type);
        field_resource_name.clear();
    }

    /**
     * Creating new directory on server.
     */
    private void createDirectory() {
        String dirName = field_resource_name.getText();
        if (!hasText(dirName)) {
            showErrorMessage("You should insert name of new directory into the filed");
            field_resource_name.clear();
            return;
        }
        mainPanelService.createDir(dirName, remoteCurrentDirectoryId);
        field_resource_name.clear();
    }

    /**
     * Removing chosen file or directory on server.
     */
    private void delete() {
        String fileName = multipleSelectionModel.getSelectedItem();
        if (!hasText(fileName)) {
            showErrorMessage("File was not chosen");
            return;
        }
        Long id;
        FileManipulationRequest.Request type;
        if (fileMap.containsKey(fileName)) {
            //it is file
            type = DELETE_FILE;
            id = fileMap.get(fileName);
        } else if (directoryMap.containsKey(fileName)) {
            //it is directory
            if (!isConfirmDeleteDirectory()) {
                return;
            }
            type = DELETE_DIR;
            id = directoryMap.get(fileName);
        } else {
            //resource is not contained into maps
            showErrorMessage("Fatal error");
            mainPanelService.exitToAuthentication(btn_info);
            return;
        }
        mainPanelService.delete(id, type);
    }

    /**
     * Downloading chosen file from server.
     */
    private void download() {
        String fileName = multipleSelectionModel.getSelectedItem();
        if (!hasText(fileName)) {
            showErrorMessage("File was not chosen");
            return;
        }
        if (!fileMap.containsKey(fileName)) {
            //it is not a file
            showErrorMessage("It is the directory\nYou can download only file\nChoose the file, please");
            return;
        }
        //Choosing directory for downloading
        File directory = directoryChooser.showDialog(null);
        if (directory == null) {
            return;
        }
        if (!directory.isDirectory()) {
            showErrorMessage("It is not valid directory");
            return;
        }
        String realFileName = fileName.substring(Constants.getFILE_NAME_PREFIX().length());
        if (Files.exists(Paths.get(directory.toString(), realFileName)) && !isConfirmOverrideFile(realFileName)) {
            return;
        }
        Long id = fileMap.get(fileName);
        mainPanelService.download(id, directory.toString());
    }

    /**
     * Uploading file to server.
     */
    private void upload() {
        File file = fileChooser.showOpenDialog(null);
        if (file == null) {
            return;
        }
        if (!file.isFile()) {
            showErrorMessage(String.format("Path \"%s\" is not file", file.toString()));
            return;
        }
        mainPanelService.sendFile(file, remoteCurrentDirectoryId);
    }

    /**
     * Updating list of files and nested directories on client side.
     */
    public void updateDirectoryEntity(CurrentDirectoryEntityList com) {
        fileMap = com.getFileMap();
        directoryMap = com.getDirectoryMap();
        label_current_dir.setText(com.getCurrentDirectory());
        remoteCurrentDirectoryId = com.getCurrentDirectoryId();

        //root directory is set only once
        if (remoteRootDirectoryId == null) {
            remoteRootDirectoryId = remoteCurrentDirectoryId;
        }

        //Creating list of all files and nested directories
        List<String> filesList = new ArrayList<>();
        filesList.addAll(fileMap.keySet());
        filesList.addAll(directoryMap.keySet());

        observableList = FXCollections.observableArrayList(filesList);
        list_files.setItems(observableList);
        multipleSelectionModel = list_files.getSelectionModel();
        if (multipleSelectionModel.getSelectedItem() == null) multipleSelectionModel.select(0);
    }

    /**
     * Handling responses from server.
     */
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
                mainPanelService.getCurrentDirectoryEntity();
                showInfoMessage(String.format("Uploading of file \"%s\" is successful", fileName));
                break;
            case DIR_CREATED:
                String directorySpecialName = Constants.getDIRECTORY_NAME_PREFIX() + fileName;
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
                handleRenameResponse(directoryMap, "directory", id, fileName, Constants.getDIRECTORY_NAME_PREFIX());
                break;
            case RENAME_FILE:
                handleRenameResponse(fileMap, "file", id, fileName, Constants.getFILE_NAME_PREFIX());
                break;
            default:
                showErrorMessage(String.format("Unknown response \"%s\" from server", response.getResponse()));
                mainPanelService.exitToAuthentication(btn_info);
        }
    }

    /**
     * Updating maps because name of file or directory was changed.
     */
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
