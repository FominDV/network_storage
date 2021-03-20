package ru.fomin.core;

import javafx.application.Platform;
import ru.fomin.need.commands.AuthResult;
import ru.fomin.need.commands.DataPackage;
import ru.fomin.need.commands.CurrentDirectoryEntityList;
import ru.fomin.need.commands.FileManipulationResponse;


import static java.lang.Thread.currentThread;

public class ResponseHandler implements Runnable {

    private final HandlerCommands handlerCommands;

    public ResponseHandler(HandlerCommands handlerCommands) {
        this.handlerCommands = handlerCommands;
    }

    @Override
    public void run() {
        while (!currentThread().isInterrupted()) {
            DataPackage response = handlerCommands.getResponseFromServer();
            if (response != null) {
                processResponse(response);
            }
        }

    }

    private void processResponse(DataPackage response) {
        Platform.runLater(() -> {
            if (response instanceof AuthResult) {
                AuthResult authResult = (AuthResult) response;
                handlerCommands.authenticationResponse(authResult);
            } else if (response instanceof CurrentDirectoryEntityList) {

                CurrentDirectoryEntityList com = (CurrentDirectoryEntityList) response;

                handlerCommands.updateDirectoryEntity(com);
            } else if (response instanceof FileManipulationResponse) {
                handlerCommands.getFileManipulationResponse((FileManipulationResponse) response);
            }
        });
    }
}
