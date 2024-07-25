package com.baiye959.mufts.utils;

import com.baiye959.mufts.network.NetworkChannel;
import com.baiye959.mufts.utils.impl.*;

/**
 * 工具类工厂
 * @author Baiye959
 */
public class UtilsFactory {
    public static JsonParserUtils createJsonParserUtils() {
        return new JsonParserUtilsImpl();
    }

    public static XmlParserUtils createXmlParserUtils() {
        return new XmlParserUtilsImpl();
    }

    public static FileEncryptionUtils createFileEncryptionUtils() {
        return new FileEncryptionUtilsImpl();
    }

    public static FileTransferUtils createFileTransferUtils(NetworkChannel networkChannel) {
        return new FileTransferUtilsImpl(networkChannel);
    }

    public static FileSaveUtils createFileSaveUtils() {
        return new FileSaveUtilsImpl();
    }

    private UtilsFactory() {}
}
