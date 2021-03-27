package ru.fomin.network;

import ru.fomin.dto.DataPackage;
import ru.fomin.services.ResponseService;


import static java.lang.Thread.currentThread;

public class ResponseReceiver implements Runnable {

    private final NetworkConnection networkConnection;
    private final ResponseService responseService;

    public ResponseReceiver() {
        networkConnection=NetworkConnection.getInstance();
        responseService = ResponseService.getInstance();
    }

    @Override
    public void run() {
        while (!currentThread().isInterrupted()) {
            DataPackage response = networkConnection.getResponseFromServer();
            if (response != null) {
                responseService.processResponse(response);
            }
        }
    }
}
