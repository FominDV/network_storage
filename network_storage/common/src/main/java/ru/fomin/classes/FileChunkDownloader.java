package ru.fomin.classes;


import ru.fomin.file_packages.FileChunkPackage;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;


public class FileChunkDownloader {

    private Path directory;
    private String filename;
    private OutputStream out;

    public void writeFileChunk(FileChunkPackage pack, Runnable saveFullAction, Path directory) throws IOException {
        filename=pack.getFilename();
        try {
            if (pack.isFirst()) {
               Path path = directory.resolve(filename);
                out = Files.newOutputStream(path);
            }

            out.write(pack.getData());

            if (pack.isLast()) {
                out.flush();
                out.close();
                saveFullAction.run();
            }
        } catch (IOException e) {
            Path path = directory.resolve(filename);
            if(Files.exists(path)){
                Files.delete(path);
            }
        }
    }

}