package com.baiye959.mufts.utils.exception;

/**
 * 错误码枚举类
 * @author Baiye959
 */

public enum ErrorCode {
    SUCCESS(0, "操作成功"),
    INVALID_COMMAND_ERROR(40001, "无效的命令"),
    INVALID_FILE_ERROR(40002, "文件格式错误"),
    FILE_NOT_FOUND_ERROR(40401, "文件未找到"),
    USER_NOT_FOUND_ERROR(40402, "用户未找到"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    CONNECTION_ERROR(50001, "连接异常"),
    ENCRYPTION_ERROR(50002, "加密失败"),
    DECRYPTION_ERROR(50003, "解密失败"),
    FILE_TRANSFER_ERROR(50004, "文件传输错误");

    private final int code;
    private final String message;
    ErrorCode(int code, String message){
        this.code = code;
        this.message = message;
    }
    public int getCode(){
        return code;
    }
    public String getMessage(){
        return message;
    }
}
