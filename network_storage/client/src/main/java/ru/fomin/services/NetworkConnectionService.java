package ru.fomin.services;

import java.nio.file.Path;

public interface NetworkConnectionService {

    /**
     * Add new information to downloading file.
     *
     * @param id   - id of file
     * @param path - path of file on client side
     */
    void putDownloadingFilesMap(Long id, Path path);

    void clearDownloadingFilesMap();

    /**
     * Closes all windows and shows the authentication window.
     */
    void exitOnFatalConnectionError();

}
