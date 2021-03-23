package ru.fomin.services.db_services;

import ru.fomin.dao.FileDataDao;
import ru.fomin.entities.Directory;
import ru.fomin.entities.FileData;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Helper class for requests to FileData entity from database.
 */
public class FileDataService {

    private static final FileDataDao FILE_DATA_DAO = new FileDataDao();

    /**
     * Creates new file.
     *
     * @param fileName  - name of new file
     * @param directory - directory for new file
     * @return - id of new file
     */
    public Long createFile(String fileName, Directory directory) {
        FileData fileData = new FileData(directory, fileName);
        return FILE_DATA_DAO.create(fileData);
    }

    /**
     * Removes file from DB.
     *
     * @param id - id of removing file
     * @return - name of removed file
     */
    public String deleteFile(Long id) {
        FileData fileData = FILE_DATA_DAO.getFile(id);
        FILE_DATA_DAO.deleteFile(fileData);
        return fileData.getName();
    }


    public FileData getFileDataById(Long id) {
        return FILE_DATA_DAO.getFile(id);
    }

    /**
     * Searches file into database and convert entity to File.
     *
     * @param id - id of searching file
     * @return - java.io.File
     */
    public File getFileById(Long id) {
        FileData fileData = getFileDataById(id);
        return new File(fileData.getDirectory().getPath() + File.separator + fileData.getName());
    }

    /**
     * Searches file into database and convert entity to Path.
     *
     * @param id - id of searching file
     * @return - java.nio.file.Path
     */
    public Path getFilePathById(Long id) {
        FileData fileData = getFileDataById(id);
        return Paths.get(fileData.getDirectory().getPath() + File.separator + fileData.getName());
    }

    /**
     * Renames file and returns its Path
     */
    public Path renameFileData(Long id, String newFileName) {
        FileData fileData = getFileDataById(id);
        fileData.setName(newFileName);
        FILE_DATA_DAO.updateFile(fileData);
        return Paths.get(fileData.getDirectory().getPath() + File.separator + newFileName);
    }
}
