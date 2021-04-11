package ru.fomin.network;

import ru.fomin.dto.DataPackage;
import ru.fomin.factory.Factory;
import ru.fomin.services.impl.ResponseService;

import static java.lang.Thread.currentThread;

/**
 * Class for accepting all messages from server.
 */
public class ResponseReceiver implements Runnable {

    private final ResponseSandler responseSandler;
    private final ResponseService responseService;

    public ResponseReceiver() {
        responseSandler = Factory.getResponseSandler();
        responseService = ResponseService.getInstance();
    }

    @Override
    public void run() {
        while (!currentThread().isInterrupted()) {
            DataPackage response = responseSandler.getResponseFromServer();
            if (response != null) {
                responseService.processResponse(response);
            }
        }
    }
}
