package ru.fomin.core;

import javafx.application.Platform;
import ru.fomin.AuthResult;
import ru.fomin.DataPackage;
import ru.fomin.FileListCommand;
import ru.fomin.gui.controllers.AuthenticationController;

import java.io.IOException;


import static java.lang.Thread.currentThread;
import static ru.fomin.AuthResult.Result.OK;

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
                handlerCommands.authenticationResponse(authResult);
        }

        if (response instanceof FileListCommand) {

            FileListCommand com = (FileListCommand) response;

           // callbackFileList.accept(com.getFileNames());
            return;
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
