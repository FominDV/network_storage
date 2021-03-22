package ru.fomin.classes;

import ru.fomin.classes.FileSendOptimizer;
import ru.fomin.commands.DataPackage;

import java.io.File;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Consumer;

import static java.lang.Thread.currentThread;

public class FileTransmitter implements Runnable {
    private final Consumer<DataPackage> sendAction;
    private final PriorityBlockingQueue<File> queue;
    private final Map<File, Long> fileDestinationMap;
    private final FileSendOptimizer fileSendOptimizer;

    private static final int MAX_COUNT = 100;


    public FileTransmitter(Consumer<DataPackage> sendAction) {
        this.sendAction=sendAction;
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
                fileSendOptimizer.sendFile(path, fileDestinationMap.get(file), sendAction);
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
