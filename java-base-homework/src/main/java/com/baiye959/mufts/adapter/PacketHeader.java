package com.baiye959.mufts.adapter;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PacketHeader {
    private String fileType;
    private long totalLength;
    private int bodyLength;
    private String fileName;

    public PacketHeader(String fileType, long totalLength, int bodyLength, String fileName) {
        this.fileType = fileType;
        this.totalLength = totalLength;
        this.bodyLength = bodyLength;
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public long getTotalLength() {
        return totalLength;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] toBytes() {
        byte[] fileNameBytes = fileName.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(4 + 8 + 4 + 4 + fileNameBytes.length);
        buffer.put(fileType.getBytes(StandardCharsets.UTF_8));
        buffer.putLong(totalLength);
        buffer.putInt(bodyLength);
        buffer.putInt(fileNameBytes.length);
        buffer.put(fileNameBytes);
        return buffer.array();
    }

    public static PacketHeader fromBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        byte[] fileTypeBytes = new byte[4];
        buffer.get(fileTypeBytes);
        String fileType = new String(fileTypeBytes, StandardCharsets.UTF_8);
        long totalLength = buffer.getLong();
        int bodyLength = buffer.getInt();
        int fileNameLength = buffer.getInt();
        byte[] fileNameBytes = new byte[fileNameLength];
        buffer.get(fileNameBytes);
        String fileName = new String(fileNameBytes, StandardCharsets.UTF_8);
        return new PacketHeader(fileType, totalLength, bodyLength, fileName);
    }
}
