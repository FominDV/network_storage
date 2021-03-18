package ru.fomin;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import ru.fomin.netty.Server;

public class NettyStart {

    public static void main(String[] args)
            throws Exception
    {
        Server server = new Server();
            server.start();
    }
}
