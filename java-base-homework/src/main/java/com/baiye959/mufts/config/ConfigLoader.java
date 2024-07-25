package com.baiye959.mufts.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

/**
 * @author Baiye959
 */
public class ConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);

    private int serverPort;
    private String clientDirectory;
    private String serverDirectory;

    public ConfigLoader() {
        loadConfig();
    }

    @SuppressWarnings("unchecked")
    private void loadConfig() {
        Yaml yaml = new Yaml();
        try (InputStream in = getClass().getResourceAsStream("/config.yml")) {
            Map<String, Object> config = yaml.load(in);
            Map<String, Object> serverConfig = (Map<String, Object>) config.get("server");
            this.serverPort = (Integer) serverConfig.get("port");
            Map<String, Object> filesConfig = (Map<String, Object>) config.get("files");
            this.clientDirectory = (String) filesConfig.get("clientDirectory");
            this.serverDirectory = (String) filesConfig.get("serverDirectory");
        } catch (Exception e) {
            logger.error("读取配置文件失败，使用默认配置");
            this.serverPort = 8080;
            this.clientDirectory = "client_files/";
            this.serverDirectory = "server_files/";
        }
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getClientDirectory() {
        return clientDirectory;
    }

    public String getServerDirectory() {
        return serverDirectory;
    }
}
