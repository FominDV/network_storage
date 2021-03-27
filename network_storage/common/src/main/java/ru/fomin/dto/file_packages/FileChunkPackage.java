package ru.fomin.dto.file_packages;


import ru.fomin.dto.DataPackage;

import java.nio.file.Path;

/**
 * DTO for transfer file by chunks
 */
public class FileChunkPackage
        extends DataPackage {

    private final String filename;
    private final byte[] data;
    private final int num;
    private final boolean last;
    //id of file or directory
    private final Long id;

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

    public String getFilename() {
        return filename;
    }

    public int getNum() {
        return num;
    }

    public byte[] getData() {
        return data;
    }

    public boolean isLast() {
        return last;
    }

    public boolean isFirst() {
        return num == 1;
    }
    public Long getId() {
        return id;
    }
}