package ru.fomin.core;

import ru.fomin.need.classes.FileSendOptimizer;

import java.io.File;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

public class FileTransmitter implements Runnable {
    private final HandlerCommands handlerCommands;
    private final PriorityBlockingQueue<File> queue;
    private final FileSendOptimizer fileSendOptimizer;

    private static final int MAX_COUNT = 100;


    public FileTransmitter(HandlerCommands handlerCommands) {
        this.handlerCommands = handlerCommands;
        queue = new PriorityBlockingQueue<>(MAX_COUNT, Comparator.comparingLong(File::length));
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
                Path path = queue.take().toPath();
                fileSendOptimizer.sendFile(path,
                        dataPackage -> handlerCommands.sendToServer(dataPackage));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void addFile(File files) {
        queue.put(files);
    }

}
