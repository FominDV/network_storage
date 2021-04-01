package ru.fomin.dto.file_packages;


import lombok.Value;
import ru.fomin.dto.DataPackage;

import java.nio.file.Path;

/**
 * DTO for transfer file by chunks
 */
@Value
public class FileChunkPackage
        extends DataPackage {

    String filename;
    byte[] data;
    int num;
    boolean last;
    //id of file or directory
    Long id;

    public FileChunkPackage(Path path, byte[] chunk, int num, Long id) {
        filename = path.getFileName().toString();
        data = chunk;
        this.num = num;
        last = false;
        this.id = id;
    }

    public FileChunkPackage(Path path, byte[] chunk, Long id) {
        filename = path.getFileName().toString();
        data = chunk;
        this.num = -1;
        last = true;
        this.id = id;
    }

    public boolean isFirst() {
        return num == 1;
    }

}