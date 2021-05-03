package ru.fomin;

import ru.fomin.util.PreparationsMaker;
import ru.fomin.server.Server;

/**
 * Start the server.
 */
public class StartServer {

    public static void main(String[] args) {

        //preparation for launch
        new PreparationsMaker().preparation();

        //start server
        new Server().start();

    }
}
