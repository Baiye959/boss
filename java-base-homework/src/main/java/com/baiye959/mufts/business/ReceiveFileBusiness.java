package com.baiye959.mufts.business;

import com.baiye959.mufts.client.ClientFrontend;
import com.baiye959.mufts.utils.FileTransferUtils;
import com.baiye959.mufts.utils.exception.ExceptionHandler;

public class ReceiveFileBusiness implements BusinessStrategy {
    private final String filePath;
    private final ClientFrontend clientFrontend;
    private final FileTransferUtils fileTransferUtils;

    public ReceiveFileBusiness(ClientFrontend clientFrontend, FileTransferUtils fileTransferUtils, String filePath) {
        this.clientFrontend = clientFrontend;
        this.fileTransferUtils = fileTransferUtils;
        this.filePath = filePath;
    }

    @Override
    public void execute() {
        try {
            clientFrontend.showProgressAlert("接收文件", "正在接收文件...", filePath);
            fileTransferUtils.receiveFile(filePath);
            clientFrontend.updateProgress(1.0);
            clientFrontend.showProgressAlert("接收文件", "文件接收完成", filePath);
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }
}
