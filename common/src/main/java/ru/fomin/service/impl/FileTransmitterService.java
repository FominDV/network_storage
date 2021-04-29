package ru.fomin.service.impl;

import ru.fomin.dto.DataPackage;
import ru.fomin.service.FileSendOptimizely;
import ru.fomin.service.FileTransmittable;

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
public class FileTransmitterService implements FileTransmittable {

    private final Consumer<DataPackage> sendAction;

    //queue of files for transfer
    private final PriorityBlockingQueue<File> queue;

    //map has file and directory's id of this file
    private final Map<File, Long> fileDestinationMap;

    private final FileSendOptimizely fileSendOptimizely;

    //max count of files into the queue
    private static final int MAX_COUNT = 100;

    /**
     * Constructor.
     *
     * @param sendAction - action for sending DTO
     */
    public FileTransmitterService(Consumer<DataPackage> sendAction) {
        this.sendAction = sendAction;
        queue = new PriorityBlockingQueue<>(MAX_COUNT, Comparator.comparingLong(File::length));
        fileDestinationMap = new HashMap<>();
        fileSendOptimizely = new FileSendOptimizerService();
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
                //delegating task of transfer to FileSendOptimizerService
                fileSendOptimizely.sendFile(path, fileDestinationMap.get(file), sendAction);
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
    @Override
    public void addFile(File file, Long directoryId) {
        queue.put(file);
        fileDestinationMap.put(file, directoryId);
    }

}
