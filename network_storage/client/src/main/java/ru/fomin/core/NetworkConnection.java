package ru.fomin.core;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import ru.fomin.commands.DataPackage;
import ru.fomin.gui.controllers.AuthenticationController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class NetworkConnection {

    private static NetworkConnection instance;
    private static String ip = "127.0.0.1";
    private static int port = 8189;
    private static MainHandler mainHandler;

    private static final int MAX_OBJ_SIZE = 10 * 1024 * 1024;

    private final ExecutorService executorService;

    private  FileTransmitter fileTransmitter;
    private  ResponseHandler responseHandler;
    private  ObjectEncoderOutputStream out;
    private  ObjectDecoderInputStream in;
    private Socket socket;

    private NetworkConnection(){
        executorService = newFixedThreadPool(2);
    }

    public static NetworkConnection getInstance(MainHandler mainHandler){
        NetworkConnection.mainHandler=mainHandler;
        if(instance==null){
            instance=new NetworkConnection();
        }
        return instance;
    }

    public void connect() throws IOException {
        socket = new Socket();
        try {
            socket = new Socket(ip, port);
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            out = new ObjectEncoderOutputStream(os);
            in = new ObjectDecoderInputStream(is, MAX_OBJ_SIZE);
            responseHandler = new ResponseHandler(this,mainHandler);
            executorService.execute(responseHandler);
            fileTransmitter = new FileTransmitter(this);
            executorService.execute(fileTransmitter);
        } catch (IOException e) {
            throw new IOException();
        }
    }

    public void closeConnection() {
        AuthenticationController.changeIsConnected();
        try {
            executorService.shutdownNow();
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToServer(DataPackage data) {
        try {
            out.writeObject(data);
            out.flush();
        } catch (IOException e) {
            exitOnFatalConnectionError();
        }
    }

    public DataPackage getResponseFromServer() {
        try {
            Object obj = in.readObject();
            return (DataPackage) obj;
        } catch (IOException | ClassNotFoundException e) {
            exitOnFatalConnectionError();
        }
        return null;
    }

    private void exitOnFatalConnectionError(){
        if (AuthenticationController.isConnected()) {
            closeConnection();
            MainHandler.exitOnFatalConnectionError();
        }
    }

    public void addFileToTransmitter(File file, Long directoryId){
        fileTransmitter.addFile(file, directoryId);
    }

    public void putDownloadingFilesMapToResponseHandler(Long id, Path path){
        responseHandler.putDownloadingFilesMap(id, path);
    }

    public static String getIp() {
        return ip;
    }

    public static void setIp(String ip) {
        NetworkConnection.ip = ip;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        NetworkConnection.port = port;
    }
}
