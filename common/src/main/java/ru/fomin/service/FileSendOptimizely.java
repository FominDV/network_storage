package ru.fomin.service;

import ru.fomin.dto.DataPackage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public interface FileSendOptimizely {

    /**
     * Analysing of file size and delegating transfer task to needed method.
     *
     * @param path        - path of the file for transfer
     * @param directoryId - id of directory that this file will be upload
     * @param sendAction  - action that will be done for transfer DTO
     */
    void sendFile(Path path, Long directoryId, Consumer<DataPackage> sendAction) throws IOException;

}
