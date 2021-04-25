package ru.fomin.service.db;

import ru.fomin.entity.Directory;
import ru.fomin.entity.FileData;

import java.nio.file.Path;
import java.util.List;

/**
 * Helper for requests to Directory entity from database.
 */
public interface DirectoryService {

    List<FileData> getFiles(Long id);

    /**
     * Returns nested directories from this directory but does not return nested directories from nested directories.
     */
    List<Directory> getNestedDirectories(Long id);

    /**
     * Verifies existing duplication of new directory and creates new directory.
     *
     * @param currentDirectory - parent directory for new directory
     * @param newDirectory     - path of new directory
     * @return - '-1' if directory already exists
     */
    Long createDirectory(Directory currentDirectory, String newDirectory);

    /**
     * Verifies existing of filename into this directory.
     */
    boolean isFileExist(String fileName, Directory directory);

    Path getDirectoryPathById(Long id);

    Directory getDirectoryById(Long id);

    /**
     * Remove directory with all entities of this directory.
     *
     * @param - id of directory
     * @return - path of removed directory
     */
    String deleteDirectory(Long id);

    /**
     * Renames directory and returns new path of this directory.
     */
    String renameDirectory(Long id, String newSimpleName);

}
