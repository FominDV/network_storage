package ru.fomin.netty;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import ru.fomin.*;
import ru.fomin.entities.Directory;
import ru.fomin.entities.FileData;
import ru.fomin.need.CurrentDirectoryEntityList;
import ru.fomin.need.GetCurrentFilesListCommand;
import ru.fomin.services.DirectoryService;
import ru.fomin.services.FileDataService;
import ru.fomin.services.UserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainHandler extends ChannelInboundHandlerAdapter {

    //services
    private static final UserService USER_SERVICE = new UserService();
    private static final DirectoryService DIRECTORY_SERVICE = new DirectoryService();
    private static final FileDataService FILE_DATA_SERVICE = new FileDataService();

    private static final String MAIN_PATH = "main_repository";
    private Directory currentDirectory;
    private FileChunkSaver saver;


    public void setUserDir(Directory directory) {
        currentDirectory = directory;
        saver = new FileChunkSaver(Paths.get(currentDirectory.getPath()));
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        try {
            if (msg instanceof GetCurrentFilesListCommand) {
                sendFileList(ctx);
                return;
            }


//            if (msg instanceof DeleteFilesCommand) {
//                deleteFiles((DeleteFilesCommand) msg);
//                sendFileList(ctx);
//                return;
//            }
//
//            if (msg instanceof FileDataPackage) {
//                saveFile((FileDataPackage) msg);
//                sendFileList(ctx);
//                return;
//            }
//
//            if (msg instanceof FileChunkPackage) {
//                Runnable action = () ->
//                {
//                    try {
//                        sendFileList(ctx);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                };
//                saver.writeFileChunk((FileChunkPackage) msg, action);
//            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }


//    private void saveFile(FileDataPackage pack)
//            throws IOException {
//        Path path = userDir.resolve(pack.getFilename());
//        Files.write(path, pack.getData());
//    }


//    private void deleteFiles(DeleteFilesCommand com)
//            throws IOException {
//        for (String fn : com.getFileNames()) {
//            Path path = userDir.resolve(fn);
//            Files.delete(path);
//        }
//    }


//    private void sendFiles(ChannelHandlerContext ctx, GetFilesCommand com)
//            throws Exception {
//        for (String fn : com.getFileNames()) {
//            Path path = userDir.resolve(fn);
//            FileSendOptimizer.sendFile(path, ctx::writeAndFlush);
//        }
//    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private void sendFileList(ChannelHandlerContext ctx) throws IOException {
        Map<String, Long> fileMap = new HashMap<>();
        Map<String, Long> directoryMap = new HashMap<>();
        Long id = currentDirectory.getId();
        List<FileData> currentFileList = DIRECTORY_SERVICE.getFiles(id);
        List<Directory> currentDirectoryList = DIRECTORY_SERVICE.getNestedDirectories(id);
        currentFileList.forEach(fileData -> fileMap.put(fileData.getName(), fileData.getId()));
        currentDirectoryList.forEach(directory -> directoryMap.put(directory.getPath().substring(currentDirectory.getPath().length()), directory.getId()));
        String currentDirectoryName = currentDirectory.getPath().substring(MAIN_PATH.length());
        ctx.writeAndFlush(new CurrentDirectoryEntityList(fileMap, directoryMap, currentDirectoryName));
    }

}