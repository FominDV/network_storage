package ru.fomin.service;

import ru.fomin.enumeration.CreatingAndUpdatingRequest;
import ru.fomin.enumeration.FileManipulateRequest;

import java.io.File;

public interface MainPanelService extends ExitService {

    void sendFile(File file, Long directoryId);
    void getCurrentDirectoryEntity();
    void download(Long id, String path);
    void delete(Long id, FileManipulateRequest type);
    void createDir(String dirName, Long remoteDirectoryId);
    void rename(String dirName, Long remoteDirectoryId, CreatingAndUpdatingRequest type);
    void moveToNestedDirectory(Long id);
    void moveFromCurrentDirectory(Long id);

}
