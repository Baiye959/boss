package com.baiye959.mufts.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NetworkChannel {
    private static final Logger logger = LoggerFactory.getLogger(NetworkChannel.class);

    private SocketChannel socketChannel;

    public NetworkChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void initClient(String host, int port) throws IOException {
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(host, port));
        while (!socketChannel.finishConnect()) {
            // 等待连接完成
        }
        logger.info("Connected to server at {}:{}", host, port);
    }

    public void sendData(ByteBuffer buffer) throws IOException {
        while (buffer.hasRemaining()) {
            socketChannel.write(buffer);
        }
        logger.info("Data sent to server");
    }

    public int receiveData(ByteBuffer buffer) throws IOException {
        int bytesRead = socketChannel.read(buffer);
        if (bytesRead == -1) {
            logger.warn("Remote host closed connection");
            socketChannel.close();
        }
        logger.info("Data received from server: {} bytes", bytesRead);
        return bytesRead;
    }

    public void close() throws IOException {
        logger.info("Closing network channel");
        socketChannel.close();
    }
}
