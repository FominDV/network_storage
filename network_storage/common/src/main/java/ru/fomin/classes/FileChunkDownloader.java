package ru.fomin.classes;


import ru.fomin.file_packages.FileChunkPackage;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Downloads chunks of the file.
 */
public class FileChunkDownloader {

    private String filename;
    private OutputStream out;

    /**
     * Processes chunk of the file for downloading.
     *
     * @param pack           - pack with bites array and information of chunk of the file
     * @param saveFullAction - actions from class that invoked this method
     * @param directory      - path of directory for saving this file
     */
    public void writeFileChunk(FileChunkPackage pack, Runnable saveFullAction, Path directory) throws IOException {
        filename = pack.getFilename();
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
            //if the file is not full and was created then this broken file will be removed
            Path path = directory.resolve(filename);
            if (Files.exists(path)) {
                Files.delete(path);
            }
        }
    }

}