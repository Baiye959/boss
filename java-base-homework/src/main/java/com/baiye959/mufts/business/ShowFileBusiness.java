package com.baiye959.mufts.business;

import com.baiye959.mufts.client.ClientFrontend;
import com.baiye959.mufts.utils.exception.ExceptionHandler;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.baiye959.mufts.utils.JsonParserUtils;
import com.baiye959.mufts.utils.UtilsFactory;
import com.baiye959.mufts.utils.XmlParserUtils;

/**
 * 展示文件业务
 * @author Baiye959
 */
public class ShowFileBusiness implements BusinessStrategy {
    private final String filePath;
    private final JsonParserUtils jsonParserUtils;
    private final XmlParserUtils xmlParserUtils;

    public ShowFileBusiness(String filePath) {
        this.filePath = filePath;
        this.jsonParserUtils = UtilsFactory.createJsonParserUtils();
        this.xmlParserUtils = UtilsFactory.createXmlParserUtils();
    }

    @Override
    public void execute() {
        try {
            Path path = Paths.get(filePath);
            byte[] data = Files.readAllBytes(path);
            String content = new String(data);

            if (filePath.endsWith(".json")) {
                content = jsonParserUtils.formatJson(content);
            } else if (filePath.endsWith(".xml")) {
                content = xmlParserUtils.formatXml(content);
            }

            ClientFrontend.displayFileContent(filePath, content);
        } catch (IOException e) {
            ExceptionHandler.handleException(e);
        }
    }
}
