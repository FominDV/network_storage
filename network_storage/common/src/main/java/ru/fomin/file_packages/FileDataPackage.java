package ru.fomin.file_packages;


import ru.fomin.commands.DataPackage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class FileDataPackage
        extends DataPackage {

    protected final String filename;
    protected final byte[] data;
    private Long directoryId;


    public FileDataPackage(Path path, Long directoryId)
            throws IOException {
        filename = path.getFileName().toString();
        this.directoryId = directoryId;
        data = Files.readAllBytes(path);
    }


    public String getFilename() {
        return filename;
    }


    public byte[] getData() {
        return data;
    }

    public Long getDirectoryId() {
        return directoryId;
    }

}