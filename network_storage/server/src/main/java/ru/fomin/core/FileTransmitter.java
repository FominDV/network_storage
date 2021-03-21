package ru.fomin.core.server;

import io.netty.channel.ChannelHandlerContext;
import ru.fomin.classes.FileSendOptimizer;

import java.io.File;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

public class FileTransmitter implements Runnable{

    private final ChannelHandlerContext context;
    private final PriorityBlockingQueue<File> queue;
    private final Map<File, Long> fileDestinationMap;
    private final FileSendOptimizer fileSendOptimizer;
   private boolean isActive = true;

    private static final int MAX_COUNT = 100;


    public FileTransmitter(ChannelHandlerContext context) {
        this.context=context;
        queue = new PriorityBlockingQueue<>(MAX_COUNT, Comparator.comparingLong(File::length));
        fileDestinationMap = new HashMap<>();
        fileSendOptimizer = new FileSendOptimizer();
    }

    @Override
    public void run() {
        try {
            while (isActive) {
                if (queue.size() == 0) {
                    Thread.sleep(1000);
                    continue;
                }
                File file = queue.take();
                Path path = file.toPath();
                fileSendOptimizer.sendFile(path, fileDestinationMap.get(file),
                        dataPackage -> context.writeAndFlush(dataPackage));
                fileDestinationMap.remove(file);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addFile(File file, Long specialId) {
        queue.put(file);
        fileDestinationMap.put(file, specialId);
    }

    public void disable(){
        isActive = false;
    }
}
