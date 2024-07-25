package com.baiye959.mufts.utils;

import java.nio.ByteBuffer;

public interface FileTransferUtils {
    void sendFile(String filePath, String receiver) throws Exception;
    void receiveFile(String filePath) throws Exception;
    void sendPacket(ByteBuffer buffer) throws Exception;
    int receivePacket(ByteBuffer buffer) throws Exception;
}
