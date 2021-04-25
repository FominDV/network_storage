package ru.fomin.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.log4j.Log4j2;
import ru.fomin.server.handler.AuthHandler;
import ru.fomin.server.handler.MainHandler;
import ru.fomin.util.PropertiesLoader;

import static io.netty.channel.ChannelOption.SO_BACKLOG;
import static io.netty.channel.ChannelOption.SO_KEEPALIVE;
import static io.netty.handler.codec.serialization.ClassResolvers.cacheDisabled;

@Log4j2
public class Server {

    private ServerBootstrap sb;
    private EventLoopGroup mainGroup;
    private EventLoopGroup workerGroup;

    private static final int PORT = PropertiesLoader.getPORT();

    public Server() {
        sb = new ServerBootstrap();
        mainGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        sb.group(mainGroup, workerGroup);
        sb.channel(NioServerSocketChannel.class);
        sb.childHandler(new SocketChannelInitializer());
        sb.option(SO_BACKLOG, 128);
        sb.childOption(SO_KEEPALIVE, true);
        try {
            Class.forName("ru.fomin.dao.SessionFactory");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start server.
     */
    public void start() {
        try {
            log.info("Server was started");
            ChannelFuture future = sb.bind(PORT).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mainGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.info("Server was stopped");
        }
    }

    private static class SocketChannelInitializer extends ChannelInitializer<SocketChannel> {

        private static final int MAX_OBJ_SIZE = 50 * 1024 * 1024;

        @Override
        protected void initChannel(SocketChannel ch) {
            ChannelHandler decoder = new ObjectDecoder(MAX_OBJ_SIZE, cacheDisabled(null));
            ChannelHandler encoder = new ObjectEncoder();
            ChannelHandler auth = new AuthHandler();
            ChannelHandler main = new MainHandler();

            ch.pipeline().addLast(decoder, encoder, auth, main);
        }
    }
}