package com.baiye959.mufts.utils.impl;

import com.baiye959.mufts.adapter.PacketHeader;
import com.baiye959.mufts.network.NetworkChannel;
import com.baiye959.mufts.utils.FileTransferUtils;
import com.baiye959.mufts.utils.FileEncryptionUtils;
import com.baiye959.mufts.utils.UtilsFactory;
import com.baiye959.mufts.utils.exception.BusinessException;
import com.baiye959.mufts.utils.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Baiye959
 */
public class FileTransferUtilsImpl implements FileTransferUtils {
    private static final int CHUNK_SIZE = 1024;
    private final FileEncryptionUtils fileEncryptionUtils;
    private final NetworkChannel networkChannel;

    public FileTransferUtilsImpl(NetworkChannel networkChannel) {
        this.fileEncryptionUtils = UtilsFactory.createFileEncryptionUtils();
        this.networkChannel = networkChannel;
    }

    @Override
    public void sendFile(String filePath, String receiver) throws Exception {
        Path path = Paths.get(filePath);
        if (!Files.exists(path) || !Files.isReadable(path)) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND_ERROR);
        }
        byte[] data = Files.readAllBytes(path);

        // 加密数据
        byte[] encryptedData = fileEncryptionUtils.encrypt(data);

        // 发送包头
        String fileType = getFileType(filePath);
        long totalLength = encryptedData.length;
        String fileName = path.getFileName().toString();
        PacketHeader header = new PacketHeader(fileType, totalLength, CHUNK_SIZE, fileName);
        sendPacket(ByteBuffer.wrap(header.toBytes()));

        // 发送文件内容
        ByteBuffer buffer = ByteBuffer.wrap(encryptedData);
        while (buffer.hasRemaining()) {
            int chunkSize = Math.min(buffer.remaining(), CHUNK_SIZE);
            ByteBuffer chunk = ByteBuffer.allocate(chunkSize);
            buffer.get(chunk.array(), 0, chunkSize);
            chunk.flip();
            sendPacket(chunk);
        }
    }

    @Override
    public void receiveFile(String filePath) throws Exception {
        ByteBuffer headerBuffer = ByteBuffer.allocate(256);
        receivePacket(headerBuffer);
        PacketHeader header = PacketHeader.fromBytes(headerBuffer.array());

        long totalBytes = header.getTotalLength();
        ByteBuffer buffer = ByteBuffer.allocate(header.getBodyLength());
        long bytesReceived = 0;
        byte[] allBytes = new byte[(int) totalBytes];

        while (bytesReceived < totalBytes) {
            int bytesRead = receivePacket(buffer);
            if (bytesRead == -1) {
                throw new BusinessException(ErrorCode.FILE_TRANSFER_ERROR);
            }
            buffer.flip();
            buffer.get(allBytes, (int) bytesReceived, bytesRead);
            bytesReceived += bytesRead;
            buffer.clear();
        }

        byte[] decryptedData = fileEncryptionUtils.decrypt(allBytes);
        Files.write(Paths.get(filePath), decryptedData);
    }

    @Override
    public void sendPacket(ByteBuffer buffer) throws Exception {
        networkChannel.sendData(buffer);
    }

    @Override
    public int receivePacket(ByteBuffer buffer) throws Exception {
        return networkChannel.receiveData(buffer);
    }

    private String getFileType(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf(".") + 1);
        return "xml".equals(extension) ? "XML" : "JSON";
    }
}
