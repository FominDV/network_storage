package ru.fomin.need.classes;


import ru.fomin.need.commands.DataPackage;
import ru.fomin.need.commands.FileChunkPackage;
import ru.fomin.need.commands.FileDataPackage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.function.Consumer;

import static java.nio.file.Files.*;


public class FileSendOptimizer {

    private static final int CHUNK_SIZE = 4 * 1024 * 1024;


    public void sendFile(Path path, Long directoryId, Consumer<DataPackage> sendAction) throws IOException {

        if (size(path) < CHUNK_SIZE * 8) {
            sendFull(path, directoryId, sendAction);
        } else {
            sendByChunks(path, directoryId, sendAction);
        }
    }


    private void sendFull(Path path, Long directoryId, Consumer<DataPackage> sendAction) throws IOException {
        DataPackage pack = new FileDataPackage(path, directoryId);
        sendAction.accept(pack);
    }


    private void sendByChunks(Path path, Long directoryId, Consumer<DataPackage> sendAction) throws IOException {
        try (InputStream in = newInputStream(path)) {
            int availCount = in.available();
            int rem = availCount % CHUNK_SIZE;

            byte[] chunk = new byte[CHUNK_SIZE];
            byte[] chunkLast = rem != 0 ? new byte[rem] : new byte[CHUNK_SIZE];

            int num = 1;
            while (availCount > CHUNK_SIZE) {
                in.read(chunk);
                DataPackage pack = new FileChunkPackage(path, chunk, num++, directoryId);
                sendAction.accept(pack);
                availCount = in.available();
            }

            in.read(chunkLast);
            DataPackage pack = new FileChunkPackage(path, chunkLast, directoryId);
            sendAction.accept(pack);
        }
    }

}