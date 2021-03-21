package ru.fomin.file_packages;


import ru.fomin.commands.DataPackage;

import java.nio.file.Path;


public class FileChunkPackage
        extends DataPackage {

    private final String filename;
    private final byte[] data;
    private final int num;
    private final boolean last;

    private final Long directoryId;


    public FileChunkPackage(Path path, byte[] chunk, int num, Long directoryId) {
        filename = path.getFileName().toString();
        data = chunk;
        this.num = num;
        last = false;
        this.directoryId = directoryId;
    }


    public FileChunkPackage(Path path, byte[] chunk, Long directoryId) {
        filename = path.getFileName().toString();
        data = chunk;
        this.num = -1;
        last = true;
        this.directoryId = directoryId;
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

    public Long getDirectoryId() {
        return directoryId;
    }
}