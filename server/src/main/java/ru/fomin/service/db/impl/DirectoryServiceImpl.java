package ru.fomin.service.db.impl;

import ru.fomin.dao.DirectoryDao;
import ru.fomin.dao.impl.DirectoryDaoImpl;
import ru.fomin.entity.Directory;
import ru.fomin.entity.FileData;
import ru.fomin.service.db.DirectoryService;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Helper class for requests to Directory entity from database.
 */
public class DirectoryServiceImpl implements DirectoryService {

    private final static DirectoryDao DIRECTORY_DAO = new DirectoryDaoImpl();

    /**
     * Returns all files from this directory.
     */
    @Override
    public List<FileData> getFiles(Long id) {
        return DIRECTORY_DAO.getFiles(id);
    }

    /**
     * Returns nested directories from this directory but does not return nested directories from nested directories.
     */
    @Override
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
    @Override
    public Long createDirectory(Directory currentDirectory, String newDirectory) {
        if (DIRECTORY_DAO.getNestedDirectories(currentDirectory.getId()).stream().anyMatch(directory -> directory.getPath().equals(newDirectory))) {
            return -1L;
        } else {
            Directory directory = new Directory(currentDirectory.getUser(), currentDirectory, newDirectory);
            return DIRECTORY_DAO.save(directory);
        }
    }

    /**
     * Verifies existing of filename into this directory.
     */
    @Override
    public boolean isFileExist(String fileName, Directory directory) {
        return getFiles(directory.getId()).
                stream().
                anyMatch(file -> file.getName().equals(fileName));
    }

    @Override
    public Path getDirectoryPathById(Long id) {
        return Paths.get(getDirectoryById(id).getPath());
    }

    @Override
    public Directory getDirectoryById(Long id) {
        return DIRECTORY_DAO.getById(id, Directory.class);
    }

    /**
     * Remove directory with all entities of this directory.
     *
     * @param - id of directory
     * @return - path of removed directory
     */
    @Override
    public String deleteDirectory(Long id) {
        Directory directory = DIRECTORY_DAO.getById(id, Directory.class);
        DIRECTORY_DAO.delete(directory);
        return directory.getPath();
    }

    /**
     * Renames directory and returns new path of this directory.
     */
    @Override
    public String renameDirectory(Long id, String newSimpleName) {
        Directory directory = DIRECTORY_DAO.getById(id, Directory.class);
        String newPath = directory.getParentDirectory().getPath() + File.separator + newSimpleName;
        directory.setPath(newPath);
        DIRECTORY_DAO.update(directory);
        return newPath;
    }
}
