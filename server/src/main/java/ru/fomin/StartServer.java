package ru.fomin;

import ru.fomin.util.PreparationsMaker;
import ru.fomin.server.Server;

public class StartServer {

    private final static String PROPERTIES_PATH = "server/src/main/resources/hibernate.properties";

    /**
     * Start the server.
     */
    public static void main(String[] args) {

        //preparation for launch
        new PreparationsMaker().preparation();

        //start server
        new Server().start();

    }
}
