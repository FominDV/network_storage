package ru.fomin.server.network;

import ru.fomin.common.KeyCommands;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class SocketHandler implements Runnable {
    private final Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    public SocketHandler(Socket socket) {
        this.socket = socket;
        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String command = in.readUTF();
                try {
                    handling(command);
                } catch (IOException e) {
                    out.writeUTF(KeyCommands.ERROR);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handling(String command) throws IOException {
        switch (command) {
            case KeyCommands.UPLOAD:
                upload();
                break;
            case KeyCommands.DOWNLOAD:
                download();
                break;
            case KeyCommands.DELETE:
                delete();
                break;
            case KeyCommands.GET_FILES:
                getFileArray();
                break;
            default:
                out.writeUTF(KeyCommands.COMMAND_ERROR);
        }
    }

    private void getFileArray() throws IOException {
        File root = new File("src/main/java/ru/fomin/server/main_repository");
        File[] filesArray = root.listFiles();
        String fileNames = "";
        for (File file:filesArray){
            fileNames+=file.getName()+KeyCommands.DELIMITER;
        }
        out.writeUTF(fileNames.substring(0,fileNames.length()-1));
    }

    private void upload() throws IOException {
        File file = new File("src/main/java/ru/fomin/server/main_repository" + File.separator + in.readUTF());
        if (!file.exists()) {
            file.createNewFile();
        }
        long size = in.readLong();
        FileOutputStream fos = new FileOutputStream(file);
        byte[] buffer = new byte[256];
        for (int i = 0; i < (size + 255) / 256; i++) { // FIXME
            int read = in.read(buffer);
            fos.write(buffer, 0, read);
        }
        fos.close();
        out.writeUTF(KeyCommands.DONE);
    }

    private void download() throws IOException {

    }

    private void delete() throws IOException {

    }
}
