package ru.fomin.services.db_services;

import ru.fomin.dao.DirectoryDao;
import ru.fomin.entities.Directory;
import ru.fomin.entities.FileData;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public Long createDirectory(Directory currentDirectory, String newDirectory) {
        if (DIRECTORY_DAO.getNestedDirectories(currentDirectory.getId()).stream().anyMatch(directory -> directory.getPath().equals(newDirectory))) {
            return -1L;
        } else {
            Directory directory = new Directory(currentDirectory.getUser(), currentDirectory, newDirectory);
            return DIRECTORY_DAO.create(directory);
        }
    }

    public boolean isFileExist(String fileName, Directory directory) {
        return getFiles(directory.getId()).
                stream().
                anyMatch(file -> file.getName().equals(fileName));
    }

    public Path getDirectoryPathById(Long id) {
        return Paths.get(getDirectoryById(id).getPath());
    }

    public Directory getDirectoryById(Long id) {
        return DIRECTORY_DAO.getDirectoryById(id);
    }

    /**
     * Remove directory with all entities of this directory.
     *
     * @param - id of directory
     * @return - name of removed directory
     */
    public String deleteDirectory(Long id) {
        Directory directory = DIRECTORY_DAO.getDirectoryById(id);
        DIRECTORY_DAO.delete(directory);
        return directory.getPath();
    }

    public String renameDirectory(Long id, String newSimpleName){
        Directory directory = DIRECTORY_DAO.getDirectoryById(id);
        String newPath = directory.getParentDirectory().getPath()+ File.separator+newSimpleName;
        directory.setPath(newPath);
        DIRECTORY_DAO.update(directory);
        return newPath;
    }
}
