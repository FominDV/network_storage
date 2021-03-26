package ru.fomin.classes;

import ru.fomin.classes.FileSendOptimizer;
import ru.fomin.commands.DataPackage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Consumer;

import static java.lang.Thread.currentThread;

/**
 * Class for thread that will be transfer files.
 */
public class FileTransmitter implements Runnable {

    private final Consumer<DataPackage> sendAction;

    //queue of files for transfer
    private final PriorityBlockingQueue<File> queue;

    //map has file and directory's id of this file
    private final Map<File, Long> fileDestinationMap;

    private final FileSendOptimizer fileSendOptimizer;

    //max count of files into the queue
    private static final int MAX_COUNT = 100;

    /**
     * Constructor.
     *
     * @param sendAction - action for sending DTO
     */
    public FileTransmitter(Consumer<DataPackage> sendAction) {
        this.sendAction = sendAction;
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
                //delegating task of transfer to FileSendOptimizer
                fileSendOptimizer.sendFile(path, fileDestinationMap.get(file), sendAction);
                fileDestinationMap.remove(file);
            }
        } catch (InterruptedException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adding file to queue and map
     */
    public void addFile(File file, Long directoryId) {
        queue.put(file);
        fileDestinationMap.put(file, directoryId);
    }

}
