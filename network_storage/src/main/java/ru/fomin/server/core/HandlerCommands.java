package ru.fomin.server.core;

import ru.fomin.common.KeyCommands;
import ru.fomin.server.network.SocketHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HandlerCommands implements Commands {

    private static final HandlerCommands handlerCommands = new HandlerCommands();
    private static final String MAIN_PATH = "src/main/java/ru/fomin/server/main_repository";

    private HandlerCommands() {
    }

    public static HandlerCommands getHandlerCommands() {
        return handlerCommands;
    }

    @Override
    public void handleRequest(String keyCommand, SocketHandler socketHandler) throws IOException {
        switch (keyCommand) {
            case KeyCommands.UPLOAD:
                upload(socketHandler);
                break;
            case KeyCommands.DOWNLOAD:
                sendFile(socketHandler);
                break;
            case KeyCommands.DELETE:
                delete(socketHandler);
                break;
            case KeyCommands.GET_FILES:
                getFileArray(socketHandler);
                break;
            default:
                socketHandler.writeUTF(KeyCommands.COMMAND_ERROR);
        }
    }

    private void getFileArray(SocketHandler socketHandler) throws IOException {
        File root = new File(MAIN_PATH);
        File[] filesArray = root.listFiles();
        String fileNames = "";
        for (File file : filesArray) {
            fileNames += file.getName() + KeyCommands.DELIMITER;
        }
        socketHandler.writeUTF(fileNames.substring(0, fileNames.length() - 1));
    }

    private void upload(SocketHandler socketHandler) throws IOException {
        int sizeOfPackage=KeyCommands.SIZE_OF_PACKAGE;
        long countOfPackages;
        File file = new File(MAIN_PATH + File.separator + socketHandler.readUTF());
        if (!file.exists()) {
            file.createNewFile();
        }
        long size = socketHandler.readLong();
        countOfPackages=(size + sizeOfPackage-1) / sizeOfPackage;
        FileOutputStream fos = new FileOutputStream(file);
        byte[] buffer = new byte[sizeOfPackage];
        for (long i = 0; i < countOfPackages; i++) { // FIXME
            int read = socketHandler.read(buffer);
            fos.write(buffer, 0, read);
        }
        fos.close();
        socketHandler.writeUTF(KeyCommands.DONE);
    }

    private void sendFile(SocketHandler socketHandler) throws IOException {
        String fileName=socketHandler.readUTF();
        File file=getFile(fileName);
        socketHandler.writeLong(file.length());
        FileInputStream fis = new FileInputStream(file);
        int read = 0;
        byte[] buffer = new byte[KeyCommands.SIZE_OF_PACKAGE];
        while ((read = fis.read(buffer)) != -1) {
            socketHandler.write(buffer, read);
        }
        socketHandler.flush();
        //for future statistical
        // socketHandler.readUTF();
    }

    private void delete(SocketHandler socketHandler) throws IOException {
        Path path = Paths.get(MAIN_PATH + File.separator + socketHandler.readUTF());
        Files.delete(path);
        socketHandler.writeUTF(KeyCommands.DONE);
    }

    private File getFile(String fileName) throws FileNotFoundException {
        for(File file: (new File(MAIN_PATH)).listFiles()){
            if(file.getName().equals(fileName)){
                return file.getAbsoluteFile();
            }
        }
        throw new FileNotFoundException();
    }
}
