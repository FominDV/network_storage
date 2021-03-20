package ru.fomin.core;

import javafx.application.Platform;
import ru.fomin.need.AuthResult;
import ru.fomin.need.DataPackage;
import ru.fomin.need.CurrentDirectoryEntityList;


import static java.lang.Thread.currentThread;
import static ru.fomin.util.ControllersUtil.showAndHideStages;

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

        if (response instanceof AuthResult) {
            AuthResult authResult = (AuthResult) response;
            Platform.runLater(() -> {
                handlerCommands.authenticationResponse(authResult);
            });

        } else if (response instanceof CurrentDirectoryEntityList) {

            CurrentDirectoryEntityList com = (CurrentDirectoryEntityList) response;

           handlerCommands.updateDirectoryEntity(com);
        }

//        if (response instanceof FileDataPackage)
//        {
//            FileDataPackage pack = (FileDataPackage) response;
//            Path path = Paths.get(STORAGE_DIR + "/" + pack.getFilename());
//            Files.write(path, pack.getData());
//            callbackFileData.run();
//            return;
//        }
//
//        if (response instanceof FileChunkPackage)
//        {
//            saver.writeFileChunk((FileChunkPackage) response,
//                    () -> callbackFileData.run());
//        }
    }
}
