package ru.fomin.services;

import ru.fomin.dao.DirectoryDao;
import ru.fomin.entities.Directory;

public class DirectoryService {
    private final static DirectoryDao DIRECTORY_DAO = new DirectoryDao();

    public static void createData(Directory data){
        DIRECTORY_DAO.create(data);
    }
}
