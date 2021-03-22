package ru.fomin.core;

import ru.fomin.classes.FileSendOptimizer;

import java.io.File;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

public class FileTransmitter implements Runnable {
    private final NetworkConnection networkConnection;
    private final PriorityBlockingQueue<File> queue;
    private final Map<File, Long> fileDestinationMap;
    private final FileSendOptimizer fileSendOptimizer;

    private static final int MAX_COUNT = 100;


    public FileTransmitter(NetworkConnection networkConnection) {
        this.networkConnection=networkConnection;
        queue = new PriorityBlockingQueue<>(MAX_COUNT, Comparator.comparingLong(File::length));
        fileDestinationMap = new HashMap<>();
        fileSendOptimizer = new FileSendOptimizer();
    }


    @Override
    public void run() {
        try {
            while (!currentThread().isInterrupted()) {
                if (queue.size() == 0) {
                    Thread.sleep(1000);
                    continue;
                }
                File file = queue.take();
                Path path = file.toPath();
                fileSendOptimizer.sendFile(path, fileDestinationMap.get(file),
                        dataPackage -> networkConnection.sendToServer(dataPackage));
                fileDestinationMap.remove(file);
            }
        } catch (InterruptedException e) {

        } catch (Exception e) {

        }

    }


    public void addFile(File file, Long directoryId) {
        queue.put(file);
        fileDestinationMap.put(file, directoryId);
    }

}
