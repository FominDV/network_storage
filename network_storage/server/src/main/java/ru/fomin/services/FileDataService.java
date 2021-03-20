package ru.fomin.services;

import ru.fomin.dao.FileDataDao;
import ru.fomin.entities.Directory;
import ru.fomin.entities.FileData;

import java.io.File;

public class FileDataService {

    private static final FileDataDao FILE_DATA_DAO = new FileDataDao();


    public Long createFile(String fileName, Directory directory) {
        FileData fileData = new FileData(directory, fileName);
        return FILE_DATA_DAO.create(fileData);
    }

    public void createFile(FileData fileData) {
        FILE_DATA_DAO.create(fileData);
    }

    //return name of removed file
    public String deleteFile(Long id) {
        FileData fileData = FILE_DATA_DAO.getFile(id);
        FILE_DATA_DAO.deleteFile(fileData);
        return fileData.getName();
    }


    public FileData getFileDataById(Long id) {
        return FILE_DATA_DAO.getFile(id);
    }

    public File getFileById(Long id) {
        FileData fileData = getFileDataById(id);
        return new File(fileData.getDirectory().getPath() + File.separator + fileData.getName());
    }
}
