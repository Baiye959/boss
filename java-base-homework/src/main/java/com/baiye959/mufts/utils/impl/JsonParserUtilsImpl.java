package com.baiye959.mufts.utils.impl;

import com.baiye959.mufts.utils.JsonParserUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Json文件解析工具实现类
 * @author Baiye959
 */
public class JsonParserUtilsImpl implements JsonParserUtils {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String formatJson(String jsonString) {
        Object jsonObject = gson.fromJson(jsonString, Object.class);
        return gson.toJson(jsonObject);
    }
}
