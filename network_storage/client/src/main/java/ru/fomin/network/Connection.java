package ru.fomin.network;

import java.io.IOException;

public interface Connection {

    /**
     * Creates connection to server.
     */
    void connect() throws IOException;

}
