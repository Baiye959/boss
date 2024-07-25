package com.baiye959.mufts.server;

import com.baiye959.mufts.adapter.PacketHeader;
import com.baiye959.mufts.utils.FileSaveUtils;
import com.baiye959.mufts.utils.UtilsFactory;
import com.baiye959.mufts.utils.exception.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ServerHandler {
    private static final int PORT = 8080;
    private static final int HEADER_SIZE = 256;

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private boolean running = true;
    private final FileSaveUtils fileSaveUtils;

    public ServerHandler() throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(PORT));
        serverSocketChannel.configureBlocking(false);
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 初始化 FileSaveUtils
        this.fileSaveUtils = UtilsFactory.createFileSaveUtils();
    }

    public void start() {
        try {
            while (running) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (key.isAcceptable()) {
                        acceptClient();
                    } else if (key.isReadable()) {
                        handleClientRequest(key);
                    }
                }
            }
        } catch (IOException e) {
            ExceptionHandler.handleException(e);
        }
    }

    private void acceptClient() throws IOException {
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);

        String welcomeMessage = "Welcome to the Server!";
        clientChannel.write(ByteBuffer.wrap(welcomeMessage.getBytes()));
    }

    private void handleClientRequest(SelectionKey key) {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer headerBuffer = ByteBuffer.allocate(HEADER_SIZE);
        ByteBuffer bodyBuffer = ByteBuffer.allocate(1024);

        try {
            // Read header
            int bytesRead = clientChannel.read(headerBuffer);
            if (bytesRead == -1) {
                clientChannel.close();
                return;
            }

            // Check if we have received the full header
            if (headerBuffer.position() < HEADER_SIZE) {
                return; // Wait for more data
            }

            headerBuffer.flip();
            PacketHeader header = PacketHeader.fromBytes(headerBuffer.array());
            headerBuffer.clear();

            long totalBytes = header.getTotalLength();
            long bytesReceived = 0;

            while (bytesReceived < totalBytes) {
                bytesRead = clientChannel.read(bodyBuffer);
                if (bytesRead == -1) {
                    clientChannel.close();
                    return;
                }
                bytesReceived += bytesRead;
                bodyBuffer.flip();

                // 保存文件
                fileSaveUtils.saveFile(header.getFileName(), bodyBuffer);

                bodyBuffer.clear(); // 清空 buffer 准备下一次读取
            }

            Path filePath = fileSaveUtils.getFilePath(header.getFileName());
            String message = "File received: " + filePath.toString();
            clientChannel.write(ByteBuffer.wrap(message.getBytes()));
        } catch (IOException e) {
            ExceptionHandler.handleException(e);
            try {
                clientChannel.close();
            } catch (IOException ex) {
                ExceptionHandler.handleException(ex);
            }
        }
    }

    public void shutdown() {
        running = false;
        try {
            selector.close();
            serverSocketChannel.close();
        } catch (IOException e) {
            ExceptionHandler.handleException(e);
        }
    }
}
