package ru.fomin.service;

import java.io.File;

public interface FileTransmittable extends Runnable {

    /**
     * Adding file to queue and map
     */
    void addFile(File file, Long directoryId);

}
