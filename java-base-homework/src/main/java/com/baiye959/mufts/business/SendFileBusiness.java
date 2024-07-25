package com.baiye959.mufts.business;

import com.baiye959.mufts.client.ClientFrontend;
import com.baiye959.mufts.utils.FileTransferUtils;
import com.baiye959.mufts.utils.exception.ExceptionHandler;

public class SendFileBusiness implements BusinessStrategy {
    private final String receiver;
    private final String filePath;
    private final ClientFrontend clientFrontend;
    private final FileTransferUtils fileTransferUtils;

    public SendFileBusiness(ClientFrontend clientFrontend, FileTransferUtils fileTransferUtils, String receiver, String filePath) {
        this.clientFrontend = clientFrontend;
        this.fileTransferUtils = fileTransferUtils;
        this.receiver = receiver;
        this.filePath = filePath;
    }

    @Override
    public void execute() {
        try {
            clientFrontend.showProgressAlert("发送文件", "正在发送文件...", filePath);
            fileTransferUtils.sendFile(filePath, receiver);
            clientFrontend.updateProgress(1.0);
            clientFrontend.showProgressAlert("发送文件", "文件发送完成", filePath);
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }
}
