package ru.fomin.network;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import ru.fomin.classes.FileTransmitter;
import ru.fomin.dto.DataPackage;
import ru.fomin.services.ResponseService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * Class for creating connection to server, sending and accepting messages.
 */
public class NetworkConnection {

    private boolean isConnected;
    private static NetworkConnection instance;
    private static String ip = "127.0.0.1";
    private static int port = 8189;

    private static final int MAX_OBJ_SIZE = 10 * 1024 * 1024;

    private ExecutorService executorService;
    private FileTransmitter fileTransmitter;
    private ResponseReceiver responseReceiver;
    private ResponseService responseService;
    private ObjectEncoderOutputStream out;
    private ObjectDecoderInputStream in;
    private Socket socket;

    private NetworkConnection() {
        responseService = ResponseService.getInstance();
    }

    public static NetworkConnection getInstance() {
        if (instance == null) {
            instance = new NetworkConnection();
        }
        return instance;
    }

    /**
     * Creating connection to server.
     */
    public void connect() throws IOException {
        if (isConnected) {
            return;
        }
        isConnected = true;
        executorService = newFixedThreadPool(2);
        socket = new Socket();
        try {
            socket = new Socket(ip, port);
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            out = new ObjectEncoderOutputStream(os);
            in = new ObjectDecoderInputStream(is, MAX_OBJ_SIZE);
            responseReceiver = new ResponseReceiver();
            executorService.execute(responseReceiver);
            fileTransmitter = new FileTransmitter(dataPackage -> sendToServer(dataPackage));
            executorService.execute(fileTransmitter);
        } catch (IOException e) {
            isConnected = false;
            throw new IOException();
        }
    }

    /**
     * Closing connection to server.
     */
    public void closeConnection() {
        isConnected = false;
        try {
            responseService.clearDownloadingFilesMap();
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

    /**
     * Closing connection and going out to authentication window.
     */
    private void exitOnFatalConnectionError() {
        if (isConnected) {
            closeConnection();
            ResponseService.exitOnFatalConnectionError();
        }
    }

    public void addFileToTransmitter(File file, Long directoryId) {
        fileTransmitter.addFile(file, directoryId);
    }

    public void putDownloadingFilesMapToResponseHandler(Long id, Path path) {
        responseService.putDownloadingFilesMap(id, path);
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
