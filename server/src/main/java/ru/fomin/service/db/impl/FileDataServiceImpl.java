package ru.fomin.service.db.impl;

import ru.fomin.dao.CommonDao;
import ru.fomin.dao.impl.CommonDaoImpl;
import ru.fomin.entity.Directory;
import ru.fomin.entity.FileData;
import ru.fomin.service.db.FileDataService;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Helper class for requests to FileData entity from database.
 */
public class FileDataServiceImpl implements FileDataService {

    private static final CommonDao FILE_DATA_DAO = new CommonDaoImpl();

    /**
     * Creates new file.
     *
     * @param fileName  - name of new file
     * @param directory - directory for new file
     * @return - id of new file
     */
    @Override
    public Long createFile(String fileName, Directory directory) {
        FileData fileData = new FileData(directory, fileName);
        return FILE_DATA_DAO.save(fileData);
    }

    /**
     * Removes file from DB.
     *
     * @param id - id of removing file
     * @return - name of removed file
     */
    @Override
    public String deleteFile(Long id) {
        FileData fileData = FILE_DATA_DAO.getById(id, FileData.class);
        FILE_DATA_DAO.delete(fileData);
        return fileData.getName();
    }

    @Override
    public FileData getFileDataById(Long id) {
        return FILE_DATA_DAO.getById(id, FileData.class);
    }

    /**
     * Searches file into database and convert entity to File.
     *
     * @param id - id of searching file
     * @return - java.io.File
     */
    @Override
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
    @Override
    public Path getFilePathById(Long id) {
        FileData fileData = getFileDataById(id);
        return Paths.get(fileData.getDirectory().getPath() + File.separator + fileData.getName());
    }

    /**
     * Renames file and returns its Path
     */
    @Override
    public Path renameFileData(Long id, String newFileName) {
        FileData fileData = getFileDataById(id);
        fileData.setName(newFileName);
        FILE_DATA_DAO.update(fileData);
        return Paths.get(fileData.getDirectory().getPath() + File.separator + newFileName);
    }
}
