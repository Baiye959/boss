package com.baiye959.mufts.server;

import com.baiye959.mufts.utils.exception.ErrorCode;
import com.baiye959.mufts.utils.exception.SystemException;

import java.io.IOException;

/**
 * 服务器启动类
 * @author Baiye959
 */
public class Server {
    public static void main(String[] args) {
        try {
            ServerHandler serverHandler = new ServerHandler();

            // 添加 JVM 关闭钩子以在 JVM 关闭时正确地关闭服务器
            Runtime.getRuntime().addShutdownHook(new Thread(serverHandler::shutdown));

            serverHandler.start();
        } catch (IOException e) {
            throw new SystemException(ErrorCode.SYSTEM_ERROR, e);
        }
    }
}
