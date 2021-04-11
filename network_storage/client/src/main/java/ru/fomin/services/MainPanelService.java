package ru.fomin.services;

import ru.fomin.dto.requests.CreatingAndUpdatingManipulationRequest;
import ru.fomin.dto.requests.FileManipulationRequest;

import java.io.File;

public interface MainPanelService extends ExitService {

    void sendFile(File file, Long directoryId);
    void getCurrentDirectoryEntity();
    void download(Long id, String path);
    void delete(Long id, FileManipulationRequest.Request type);
    void createDir(String dirName, Long remoteDirectoryId);
    void rename(String dirName, Long remoteDirectoryId, CreatingAndUpdatingManipulationRequest.Type type);

}
