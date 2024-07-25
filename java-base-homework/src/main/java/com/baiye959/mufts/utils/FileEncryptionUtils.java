package com.baiye959.mufts.utils;

import com.baiye959.mufts.utils.exception.BusinessException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 文件加解密工具
 * @author Baiye959
 */
public interface FileEncryptionUtils {
    byte[] encrypt(byte[] data) throws BusinessException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException;
    byte[] decrypt(byte[] encryptedData) throws BusinessException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException;
}
