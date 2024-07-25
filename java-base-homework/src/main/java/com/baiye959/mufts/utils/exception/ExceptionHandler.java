package com.baiye959.mufts.utils.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 异常处理器
 * @author Baiye959
 */
public class ExceptionHandler {
    // 私有构造函数，防止实例化
    private ExceptionHandler() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    public static void handleException(Throwable e) {
        if (e instanceof BusinessException) {
            handleBusinessException((BusinessException) e);
        } else if (e instanceof SystemException) {
            handleSystemException((SystemException) e);
        } else if (e instanceof NullPointerException) {
            handleNullPointerException((NullPointerException) e);
        } else if (e instanceof IOException) {
            handleIOException((IOException) e);
        } else {
            handleGenericException(e);
        }
    }

    private static void handleBusinessException(BusinessException e) {
        logger.error("BusinessException: Code - {}, Message - {}", e.getErrorCode().getCode(), e.getErrorCode().getMessage());
    }

    private static void handleSystemException(SystemException e) {
        logger.error("SystemException: Code - {}, Message - {}", e.getErrorCode().getCode(), e.getErrorCode().getMessage());
    }

    private static void handleNullPointerException(NullPointerException e) {
        logger.error("NullPointerException: ", e);
    }

    private static void handleIOException(IOException e) {
        logger.error("IOException: ", e);
    }

    private static void handleGenericException(Throwable e) {
        logger.error("Exception: ", e);
    }
}
