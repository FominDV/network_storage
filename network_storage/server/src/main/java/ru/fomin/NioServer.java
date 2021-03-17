package ru.fomin;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class NioServer {

   public void run(){
       EventLoopGroup bossGroup = new NioEventLoopGroup(1);
       EventLoopGroup workerGroup = new NioEventLoopGroup();
   }

    public static void main(String[] args) {
        new NioServer().run();
    }
}
