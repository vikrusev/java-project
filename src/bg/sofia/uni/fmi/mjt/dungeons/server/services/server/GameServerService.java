package bg.sofia.uni.fmi.mjt.dungeons.server.services.server;

import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.player.Player;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GameServerService implements AutoCloseable {

    protected ServerSocketChannel serverSocketChannel;
    protected Selector selector;
    protected static ByteBuffer commandBuffer;

    protected boolean runServer;

    protected static ConcurrentHashMap<SocketChannel, Player> users = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<SocketChannel, Player> getUsers() {
        return users;
    }

    protected GameServerService(int port) throws IOException {
        this.runServer = true;
        this.selector = Selector.open();

        final int bufferSize = 1024;
        commandBuffer = ByteBuffer.allocate(bufferSize);

        this.serverSocketChannel = ServerSocketChannel.open();
        this.configureServerSocketChannel(port);
    }

    private void configureServerSocketChannel(int port) throws IOException {
        this.serverSocketChannel.socket().bind(new InetSocketAddress(port));
        this.serverSocketChannel.configureBlocking(false);

        this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public abstract void close() throws Exception;
}
