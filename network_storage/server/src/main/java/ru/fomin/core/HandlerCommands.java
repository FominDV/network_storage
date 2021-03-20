package ru.fomin.core;

import ru.fomin.KeyCommands;
import ru.fomin.entities.Directory;
import ru.fomin.entities.FileData;
import ru.fomin.network.SocketHandler;
import ru.fomin.services.DirectoryService;
import ru.fomin.services.FileDataService;
import ru.fomin.services.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class HandlerCommands implements Commands {
    private Directory currentDirectory;
    private String rootDirectory;

    //services
    private static final UserService USER_SERVICE = new UserService();
    private static final DirectoryService DIRECTORY_SERVICE = new DirectoryService();
    private static final FileDataService FILE_DATA_SERVICE = new FileDataService();
    private static final String MAIN_PATH = "main_repository" + File.separator;

    @Override
    synchronized public void handleRequest(String keyCommand, SocketHandler socketHandler) throws IOException {
        switch (keyCommand) {
            case KeyCommands.UPLOAD:
                download(socketHandler);
                break;
            case KeyCommands.DOWNLOAD:
                sendFile(socketHandler);
                break;
            case KeyCommands.DELETE_FILE:
                deleteFile(socketHandler);
                break;
            case KeyCommands.DELETE_DIR:
                deleteDirectory(socketHandler);
                break;
            case KeyCommands.GET_FILES:
                getFileArray(socketHandler);
                break;
            case KeyCommands.REGISTRATION:
                registration(socketHandler);
                break;
            case KeyCommands.AUTHENTICATION:
                authentication(socketHandler);
                break;
            case KeyCommands.GET_CURRENT_DIR:
                getCurrentDirectory(socketHandler);
                break;
            case KeyCommands.CREATE_DIRECTORY:
                createDirectory(socketHandler);
                break;
            default:
                socketHandler.writeUTF(KeyCommands.COMMAND_ERROR);
        }
    }

    private void deleteDirectory(SocketHandler socketHandler) throws IOException {

    }

    private void createDirectory(SocketHandler socketHandler) throws IOException {
        String newDirectory = currentDirectory.getPath() + File.separator + socketHandler.readUTF();
        if (DIRECTORY_SERVICE.createDirectory(currentDirectory, newDirectory)) {
            Files.createDirectory(Paths.get(newDirectory));
            socketHandler.writeUTF(KeyCommands.DONE);
        } else {
            socketHandler.writeUTF(KeyCommands.ALREADY_EXIST);
        }
    }

    private void getCurrentDirectory(SocketHandler socketHandler) throws IOException {
        String formattedCurrentDirectory = currentDirectory.getPath().substring(MAIN_PATH.length());
        socketHandler.writeUTF(formattedCurrentDirectory);
    }

    private void authentication(SocketHandler socketHandler) throws IOException {
        String login = socketHandler.readUTF();
        String password = socketHandler.readUTF();
        if (USER_SERVICE.isValidUserData(login, password)) {
            currentDirectory = USER_SERVICE.getUserByLogin(login).getRootDirectory();
            rootDirectory = currentDirectory.getPath();
            socketHandler.writeUTF(KeyCommands.DONE);
        } else {
            socketHandler.writeUTF(KeyCommands.ERROR);
        }
    }

    private void registration(SocketHandler socketHandler) throws IOException {
        String login = socketHandler.readUTF();
        String password = socketHandler.readUTF();
        String root = MAIN_PATH + File.separator + login;
        if (USER_SERVICE.createUser(login, password, root)) {
            Files.createDirectory(Paths.get(root));
            socketHandler.writeUTF(KeyCommands.DONE);
        } else {
            socketHandler.writeUTF(KeyCommands.DUPLICATED_LOGIN);
        }
    }

    synchronized private void getFileArray(SocketHandler socketHandler) throws IOException {
        Long id = currentDirectory.getId();
        List<FileData> currentFileList = DIRECTORY_SERVICE.getFiles(id);
        List<Directory> currentDirectoryList = DIRECTORY_SERVICE.getNestedDirectories(id);
        if (currentFileList.size() == 0 && currentDirectoryList.size() == 0) {
            socketHandler.writeUTF("");
            return;
        }
        StringBuffer fileNames = new StringBuffer(200);
        for (FileData file : currentFileList) {
            fileNames.append(file.getName());
            fileNames.append(KeyCommands.DELIMITER);
            fileNames.append(file.getId());
            fileNames.append(KeyCommands.DELIMITER);
        }
        fileNames.append(KeyCommands.HARD_DELIMITER);
        String directoryName;
        for (Directory directory : currentDirectoryList) {
            directoryName = new File(directory.getPath()).getName();
            fileNames.append(KeyCommands.DELIMITER);
            fileNames.append(directoryName);
            fileNames.append(KeyCommands.DELIMITER);
            fileNames.append(directory.getId());
        }
        socketHandler.writeUTF(fileNames.toString());
    }

    synchronized private void download(SocketHandler socketHandler) throws IOException {
        int sizeOfPackage = KeyCommands.SIZE_OF_PACKAGE;
        long countOfPackages;
        String fileName = socketHandler.readUTF();
        if (DIRECTORY_SERVICE.getFiles(currentDirectory.getId()).
                stream().
                anyMatch(file -> file.getName().equals(fileName))) {
            socketHandler.writeUTF(KeyCommands.ALREADY_EXIST);
            return;
        }
        socketHandler.writeUTF(KeyCommands.DONE);
        File file = new File(currentDirectory.getPath() + File.separator + fileName);
        long size = socketHandler.readLong();
        countOfPackages = (size + sizeOfPackage - 1) / sizeOfPackage;
        FileOutputStream fos = new FileOutputStream(file);
        byte[] buffer = new byte[sizeOfPackage];
        for (long i = 0; i < countOfPackages; i++) {
            int read = socketHandler.read(buffer);
            fos.write(buffer, 0, read);
        }
        fos.close();
        socketHandler.flush();
        FILE_DATA_SERVICE.createFile(fileName, currentDirectory);
        socketHandler.writeUTF(KeyCommands.DONE);
    }

    synchronized private void sendFile(SocketHandler socketHandler) throws IOException {
        Long id = socketHandler.readLong();
        String filePath = currentDirectory.getPath() + File.separator + FILE_DATA_SERVICE.getFileDataById(id).getName();
        File file = new File(filePath);
        socketHandler.writeLong(file.length());
        FileInputStream fis = new FileInputStream(file);
        int read = 0;
        byte[] buffer = new byte[KeyCommands.SIZE_OF_PACKAGE];
        while ((read = fis.read(buffer)) != -1) {
            socketHandler.write(buffer, read);
        }
        socketHandler.flush();
        fis.close();
    }

    synchronized private void deleteFile(SocketHandler socketHandler) throws IOException {
        Long id = socketHandler.readLong();
        String fileName = FILE_DATA_SERVICE.deleteFile(id);
        Path path = Paths.get(currentDirectory.getPath(), fileName);
        Files.delete(path);
        socketHandler.writeUTF(KeyCommands.DONE);
    }
}

