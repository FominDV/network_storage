package ru.fomin.dao;

import ru.fomin.entity.Directory;
import ru.fomin.entity.FileData;

import java.util.List;

public interface DirectoryDao extends CommonDao{

    List<FileData> getFiles(Long id);

    List<Directory> getNestedDirectories(Long id);

}
