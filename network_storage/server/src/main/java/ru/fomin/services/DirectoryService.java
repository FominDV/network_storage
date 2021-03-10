package ru.fomin.services;

import ru.fomin.dao.DirectoryDao;
import ru.fomin.entities.Directory;
import ru.fomin.entities.FileData;

import java.util.List;

public class DirectoryService {
    private final static DirectoryDao DIRECTORY_DAO = new DirectoryDao();

    public void createDirectory(Directory directory) {
        DIRECTORY_DAO.create(directory);
    }

    public List<FileData> getFiles(Long id) {
        return DIRECTORY_DAO.getFiles(id);
    }

    public List<Directory> getNestedDirectories(Long id) {
        return DIRECTORY_DAO.getNestedDirectories(id);
    }

    public boolean createDirectory(Directory currentDirectory, String newDirectory) {
        if(DIRECTORY_DAO.getNestedDirectories(currentDirectory.getId()).stream().anyMatch(directory -> directory.getPath().equals(newDirectory))){
            return false;
        }else {
            Directory directory=new Directory(currentDirectory.getUser(),currentDirectory,newDirectory);
            DIRECTORY_DAO.create(directory);
            return true;
        }
    }
}
