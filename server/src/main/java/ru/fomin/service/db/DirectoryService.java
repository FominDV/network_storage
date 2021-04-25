package ru.fomin.service.db;

import ru.fomin.dao.DirectoryDao;
import ru.fomin.entity.Directory;
import ru.fomin.entity.FileData;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Helper class for requests to Directory entity from database.
 */
public class DirectoryService {

    private final static DirectoryDao DIRECTORY_DAO = new DirectoryDao();

    /**
     * Returns all files from this directory.
     */
    public List<FileData> getFiles(Long id) {
        return DIRECTORY_DAO.getFiles(id);
    }

    /**
     * Returns nested directories from this directory but does not return nested directories from nested directories.
     */
    public List<Directory> getNestedDirectories(Long id) {
        return DIRECTORY_DAO.getNestedDirectories(id);
    }

    /**
     * Verifies existing duplication of new directory and creates new directory.
     *
     * @param currentDirectory - parent directory for new directory
     * @param newDirectory     - path of new directory
     * @return - '-1' if directory already exists
     */
    public Long createDirectory(Directory currentDirectory, String newDirectory) {
        if (DIRECTORY_DAO.getNestedDirectories(currentDirectory.getId()).stream().anyMatch(directory -> directory.getPath().equals(newDirectory))) {
            return -1L;
        } else {
            Directory directory = new Directory(currentDirectory.getUser(), currentDirectory, newDirectory);
            return DIRECTORY_DAO.create(directory);
        }
    }

    /**
     * Verifies existing of filename into this directory.
     */
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
     * @return - path of removed directory
     */
    public String deleteDirectory(Long id) {
        Directory directory = DIRECTORY_DAO.getDirectoryById(id);
        DIRECTORY_DAO.delete(directory);
        return directory.getPath();
    }

    /**
     * Renames directory and returns new path of this directory.
     */
    public String renameDirectory(Long id, String newSimpleName) {
        Directory directory = DIRECTORY_DAO.getDirectoryById(id);
        String newPath = directory.getParentDirectory().getPath() + File.separator + newSimpleName;
        directory.setPath(newPath);
        DIRECTORY_DAO.update(directory);
        return newPath;
    }
}
