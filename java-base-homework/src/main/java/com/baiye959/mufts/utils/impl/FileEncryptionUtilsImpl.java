package com.baiye959.mufts.utils.impl;

import com.baiye959.mufts.utils.FileEncryptionUtils;
import com.baiye959.mufts.utils.exception.BusinessException;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 文件加解密工具类实现类
 * @author Baiye959
 */
public class FileEncryptionUtilsImpl implements FileEncryptionUtils {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    private static final SecretKey SECRET_KEY = generateKey();

    private static SecretKey generateKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            // for AES-256
            keyGen.init(256);
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating key", e);
        }
    }

    @Override
    public byte[] encrypt(byte[] data) throws BusinessException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY);
        return Base64.encodeBase64(cipher.doFinal(data));
    }

    @Override
    public byte[] decrypt(byte[] encryptedData) throws BusinessException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY);
        return cipher.doFinal(Base64.decodeBase64(encryptedData));
    }
}
