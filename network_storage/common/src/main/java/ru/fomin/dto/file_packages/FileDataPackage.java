package ru.fomin.dto.file_packages;


import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import ru.fomin.dto.DataPackage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * DTO for transfer file by one piece.
 */
@Value
public class FileDataPackage extends DataPackage {

    String filename;
    byte[] data;
    Long directoryId;

    public FileDataPackage(Path path, Long directoryId) throws IOException {
        filename = path.getFileName().toString();
        this.directoryId = directoryId;
        data = Files.readAllBytes(path);
    }

}