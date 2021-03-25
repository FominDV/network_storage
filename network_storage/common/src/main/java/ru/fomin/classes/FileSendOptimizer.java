package ru.fomin.classes;


import ru.fomin.commands.DataPackage;
import ru.fomin.file_packages.FileChunkPackage;
import ru.fomin.file_packages.FileDataPackage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.function.Consumer;

import static java.nio.file.Files.*;

/**
 * Class for analysing of file size and choosing method for transfer.
 */
public class FileSendOptimizer {

    private static final int CHUNK_SIZE = 4 * 1024 * 1024;

    /**
     * Analysing of file size and delegating transfer task to needed method.
     *
     * @param path        - path of the file for transfer
     * @param directoryId - id of directory that this file will be upload
     * @param sendAction  - action that will be done for transfer DTO
     */
    public void sendFile(Path path, Long directoryId, Consumer<DataPackage> sendAction) throws IOException {
        if (size(path) < CHUNK_SIZE * 8) {
            sendFull(path, directoryId, sendAction);
        } else {
            sendByChunks(path, directoryId, sendAction);
        }
    }

    /**
     * Transfer file by one DTO.
     */
    private void sendFull(Path path, Long directoryId, Consumer<DataPackage> sendAction) throws IOException {
        DataPackage pack = new FileDataPackage(path, directoryId);
        sendAction.accept(pack);
    }

    /**
     * Transfer file by several DTO.
     */
    private void sendByChunks(Path path, Long directoryId, Consumer<DataPackage> sendAction) throws IOException {
        try (InputStream in = newInputStream(path)) {
            int availableBytes = in.available();
            int bytesOfLastPackage = availableBytes % CHUNK_SIZE;

            byte[] chunk = new byte[CHUNK_SIZE];
            byte[] chunkLast = bytesOfLastPackage != 0 ? new byte[bytesOfLastPackage] : new byte[CHUNK_SIZE];

            int num = 1;
            while (availableBytes > CHUNK_SIZE) {
                in.read(chunk);
                DataPackage pack = new FileChunkPackage(path, chunk, num++, directoryId);
                sendAction.accept(pack);
                availableBytes = in.available();
            }

            in.read(chunkLast);
            DataPackage pack = new FileChunkPackage(path, chunkLast, directoryId);
            sendAction.accept(pack);
        }
    }

}