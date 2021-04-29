package ru.fomin.service;

import ru.fomin.dto.file_packages.FileChunkPackage;

import java.io.IOException;
import java.nio.file.Path;

public interface FileChunkDownloadable {

    /**
     * Processes chunk of the file for downloading.
     *
     * @param pack           - pack with bites array and information of chunk of the file
     * @param saveFullAction - actions from class that invoked this method
     * @param directory      - path of directory for saving this file
     */
    void writeFileChunk(FileChunkPackage pack, Runnable saveFullAction, Path directory) throws IOException;

}
