package ru.fomin.dto.file_packages;


import ru.fomin.dto.DataPackage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * DTO for transfer file by one piece.
 */
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