package ru.fomin.service.db;

import ru.fomin.entity.Directory;
import ru.fomin.entity.FileData;

import java.io.File;
import java.nio.file.Path;

/**
 * Helper for requests to FileData entity from database.
 */
public interface FileDataService {

    /**
     * Creates new file.
     *
     * @param fileName  - name of new file
     * @param directory - directory for new file
     * @return - id of new file
     */
    Long createFile(String fileName, Directory directory);

    /**
     * Removes file from DB.
     *
     * @param id - id of removing file
     * @return - name of removed file
     */
    String deleteFile(Long id);

    FileData getFileDataById(Long id);

    /**
     * Searches file into database and convert entity to File.
     *
     * @param id - id of searching file
     * @return - java.io.File
     */
    File getFileById(Long id);

    /**
     * Searches file into database and convert entity to Path.
     *
     * @param id - id of searching file
     * @return - java.nio.file.Path
     */
    Path getFilePathById(Long id);

    /**
     * Renames file and returns its Path
     */
    Path renameFileData(Long id, String newFileName);

}
