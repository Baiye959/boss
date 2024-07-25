package com.baiye959.mufts.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;

public interface FileSaveUtils {
    Path saveFile(String fileName, ByteBuffer buffer) throws IOException;
    Path getFilePath(String fileName);
}
