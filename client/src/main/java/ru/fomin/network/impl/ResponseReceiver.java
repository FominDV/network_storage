package ru.fomin.network.impl;

import ru.fomin.dto.DataPackage;
import ru.fomin.factory.Factory;
import ru.fomin.network.ResponseSandler;
import ru.fomin.service.ResponseProcessor;

import static java.lang.Thread.currentThread;

/**
 * Class for accepting all messages from server.
 */
public class ResponseReceiver implements Runnable {

    private final ResponseSandler responseSandler;
    private final ResponseProcessor responseProcessor;

    public ResponseReceiver() {
        responseSandler = Factory.getResponseSandler();
        responseProcessor = Factory.getResponseProcessor();
    }

    @Override
    public void run() {
        while (!currentThread().isInterrupted()) {
            DataPackage response = responseSandler.getResponseFromServer();
            if (response != null) {
                responseProcessor.processResponse(response);
            }
        }
    }
}
