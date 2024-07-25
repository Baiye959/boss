package com.baiye959.mufts.utils.impl;

import com.baiye959.mufts.utils.FileSaveUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.*;

public class FileSaveUtilsImpl implements FileSaveUtils {
    private static final String BASE_DIR = "server_files";

    static {
        // 确保固定目录存在
        Path baseDirPath = Paths.get(BASE_DIR);
        try {
            if (!Files.exists(baseDirPath)) {
                Files.createDirectory(baseDirPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to create base directory: " + BASE_DIR, e);
        }
    }

    @Override
    public Path saveFile(String fileName, ByteBuffer buffer) throws IOException {
        // 确保文件名不包含路径部分
        System.out.println(fileName);
        String cleanFileName = Paths.get(fileName).getFileName().toString();
        Path filePath = Paths.get(BASE_DIR, cleanFileName);

        System.out.println("Saving file: " + filePath);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        Files.createFile(filePath);

        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        Files.write(filePath, bytes, StandardOpenOption.APPEND);

        return filePath;
    }

    @Override
    public Path getFilePath(String fileName) {
        String cleanFileName = Paths.get(fileName).getFileName().toString();
        return Paths.get(BASE_DIR, cleanFileName);
    }
}
