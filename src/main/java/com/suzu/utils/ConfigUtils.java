package com.suzu.utils;

import java.util.Properties;

public class ConfigUtils {
    Properties configFile;

    public ConfigUtils() {
        configFile = new Properties();
        try {
            configFile.load(this.getClass().getClassLoader().
                    getResourceAsStream("myapp/config.cfg"));
        } catch (Exception eta) {
            eta.printStackTrace();
        }
    }

    public String getProperty(String key) {
        String value = this.configFile.getProperty(key);
        return value;
    }
}
