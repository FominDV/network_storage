package ru.fomin.network.impl;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import lombok.Getter;
import lombok.Setter;
import ru.fomin.rervice.FileTransmitterService;
import ru.fomin.dto.DataPackage;
import ru.fomin.factory.Factory;
import ru.fomin.network.Connection;
import ru.fomin.network.ResponseSandler;
import ru.fomin.service.NetworkConnectionService;

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
public class NetworkConnection implements Connection, ResponseSandler {

    private boolean isConnected;
    private static NetworkConnection instance;

    @Setter
    @Getter
    private static String ip = "127.0.0.1";

    @Setter
    @Getter
    private static int port = 8189;

    private static final int MAX_OBJ_SIZE = 10 * 1024 * 1024;

    private ExecutorService executorService;
    private FileTransmitterService fileTransmitterService;
    private ResponseReceiver responseReceiver;
    private NetworkConnectionService networkConnectionService;
    private ObjectEncoderOutputStream out;
    private ObjectDecoderInputStream in;
    private Socket socket;

    private NetworkConnection() {
        networkConnectionService = Factory.getNetworkConnectionService();
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
    @Override
    public void connect() throws IOException {
        if (isConnected) {
            return;
        }
        isConnected = true;
        executorService = newFixedThreadPool(2);
        createSocketAndStreams();
        responseReceiver = new ResponseReceiver();
        executorService.execute(responseReceiver);
        fileTransmitterService = new FileTransmitterService(dataPackage -> sendToServer(dataPackage));
        executorService.execute(fileTransmitterService);
    }

    /**
     * Closing connection to server.
     */
    public void closeConnection() {
        isConnected = false;
        try {
            networkConnectionService.clearDownloadingFilesMap();
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
            networkConnectionService.exitOnFatalConnectionError();
        }
    }

    public void addFileToTransmitter(File file, Long directoryId) {
        fileTransmitterService.addFile(file, directoryId);
    }

    public void putDownloadingFilesMapToResponseHandler(Long id, Path path) {
        networkConnectionService.putDownloadingFilesMap(id, path);
    }

    private void createSocketAndStreams() throws IOException {
        try {
            socket = new Socket(ip, port);
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            out = new ObjectEncoderOutputStream(os);
            in = new ObjectDecoderInputStream(is, MAX_OBJ_SIZE);
        } catch (IOException e) {
            isConnected = false;
            throw new IOException();
        }
    }
}
