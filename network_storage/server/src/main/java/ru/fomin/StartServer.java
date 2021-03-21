package ru.fomin;

import ru.fomin.core.Server;

public class StartServer {

    public static void main(String[] args)
            throws Exception
    {
        Server server = new Server();
            server.start();
    }
}
