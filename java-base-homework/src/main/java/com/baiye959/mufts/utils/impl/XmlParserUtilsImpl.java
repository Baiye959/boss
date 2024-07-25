package com.baiye959.mufts.utils.impl;

import com.baiye959.mufts.utils.XmlParserUtils;
import com.baiye959.mufts.utils.exception.BusinessException;
import com.baiye959.mufts.utils.exception.ErrorCode;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.StringReader;
import java.io.StringWriter;

/**
 * Xml文件解析工具实现类
 * @author Baiye959
 */
public class XmlParserUtilsImpl implements XmlParserUtils {
    @Override
    public String formatXml(String xmlString) {
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(new StringReader(xmlString));

            Format format = Format.getPrettyFormat();
            format.setIndent("    ");
            format.setLineSeparator(System.lineSeparator());

            XMLOutputter xmlOutputter = new XMLOutputter(format);
            StringWriter stringWriter = new StringWriter();
            xmlOutputter.output(document, stringWriter);

            return stringWriter.toString();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_FILE_ERROR);
        }
    }
}