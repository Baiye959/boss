package com.baiye959.mufts.client;

import com.baiye959.mufts.business.*;
import com.baiye959.mufts.config.ConfigLoader;
import com.baiye959.mufts.network.NetworkChannel;
import com.baiye959.mufts.utils.FileTransferUtils;
import com.baiye959.mufts.utils.UtilsFactory;
import com.baiye959.mufts.utils.exception.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.IOException;

public class ClientHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private final NetworkChannel networkChannel;
    private BusinessStrategy businessStrategy;
    private volatile boolean running = true;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ClientFrontend clientFrontend;
    private final Selector selector;
    private final FileTransferUtils fileTransferUtils;

    public ClientHandler(String username, ClientFrontend clientFrontend) throws IOException {
        this.clientFrontend = clientFrontend;
        SocketChannel socketChannel = SocketChannel.open();
        this.networkChannel = new NetworkChannel(socketChannel);
        ConfigLoader configLoader = new ConfigLoader();
        networkChannel.initClient("localhost", configLoader.getServerPort());
        this.fileTransferUtils = UtilsFactory.createFileTransferUtils(networkChannel);
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_READ);
        startListening();
    }

    private void startListening() {
        executorService.submit(() -> {
            while (running) {
                try {
                    selector.select();
                    processSelectedKeys();
                } catch (IOException e) {
                    ExceptionHandler.handleException(e);
                }
            }
        });
    }

    private void processSelectedKeys() throws IOException {
        Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
        while (keys.hasNext()) {
            SelectionKey key = keys.next();
            keys.remove();
            if (key.isReadable()) {
                handleReadableKey(key);
            }
        }
    }

    private void handleReadableKey(SelectionKey key) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = receiveFromServer(buffer);
        if (bytesRead > 0) {
            handleReceivedData(buffer.array(), bytesRead);
        } else if (bytesRead == -1) {
            logger.warn("Stopping due to connection close");
            stop();
        }
    }

    private int receiveFromServer(ByteBuffer buffer) throws IOException {
        return networkChannel.receiveData(buffer);
    }

    private void handleReceivedData(byte[] data, int length) {
        String message = new String(data, 0, length).trim();
        if (message.startsWith("RECEIVE")) {
            String filePath = message.split(" ", 2)[1];
            receiveFile(filePath);
        } else {
            logger.info("Received message: {}", message);
        }
    }

    private void receiveFile(String filePath) {
        try {
            businessStrategy = new ReceiveFileBusiness(clientFrontend, fileTransferUtils, filePath);
            businessStrategy.execute();
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }

    public void handleRequest(String command) {
        String[] parts = command.split(" ", 3);
        String action = parts[0];

        switch (action) {
            case "send":
                if (parts.length < 3) {
                    clientFrontend.setStatusLabel("发送命令格式错误", "red");
                    return;
                }
                String receiver = parts[1];
                String filePath = parts[2];
                handleSendFile(receiver, filePath);
                break;
            case "show":
                if (parts.length < 2) {
                    clientFrontend.setStatusLabel("显示命令格式错误", "red");
                    return;
                }
                String path = parts[1];
                handleShowFile(path);
                break;
            default:
                clientFrontend.setStatusLabel("未知命令", "red");
        }
    }

    private void handleSendFile(String receiver, String filePath) {
        businessStrategy = new SendFileBusiness(clientFrontend, fileTransferUtils, receiver, filePath);
        businessStrategy.execute();
    }

    private void handleShowFile(String filePath) {
        businessStrategy = new ShowFileBusiness(filePath);
        businessStrategy.execute();
    }

    public void stop() {
        running = false;
        executorService.shutdown();
        try {
            networkChannel.close();
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }
}
