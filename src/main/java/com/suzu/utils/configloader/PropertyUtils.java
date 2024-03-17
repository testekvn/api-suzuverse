package com.suzu.utils.configloader;

import com.suzu.constants.ConfigProperties;
import com.suzu.constants.FrameworkConst;
import com.suzu.database.DatabaseInfo;
import com.suzu.database.DatabaseType;
import com.suzu.exceptions.PropertyFileUsageException;
import com.suzu.utils.LanguageUtils;
import com.suzu.utils.Log;
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static java.util.Locale.ENGLISH;
import static java.util.Locale.JAPANESE;

@Data
public class PropertyUtils {
    private static ResourceBundle resourceConfig;
    private static Properties properties;

    private static void loadLanguageBundle(String language) {
        Locale locale = new Locale("vi", "VI");
        switch (language.toLowerCase()) {
            case "en":
                locale = ENGLISH;
                break;
            case "ja":
                locale = JAPANESE;
                break;
            case "vi":
            default:    // Default value: VI
                break;
        }
        resourceConfig = ResourceBundle.getBundle("language", locale);
    }

    /**
     * Get language for application
     *
     * @param key : Language keyword
     * @return : The value of language keyword
     */
    public static String getLanguageValue(String key) {
        try {
            if (resourceConfig == null)
                loadLanguageBundle(properties.getProperty("language"));
            return resourceConfig.getString(key);
        } catch (Exception e) {
            return "LANGUAGE_NOT_FOUND";
        }
    }

    /**
     * Load all properties for the testing project.
     */
    public static synchronized Properties loadAllProperties(JSONObject envConfiguration) {
        // Add all property
        List<String> propertyFiles = new ArrayList<>();
        propertyFiles.add("src/test/resources/config/config.properties");


        if (Objects.nonNull(properties)) return properties;
        properties = new Properties();
        Properties tmpProperty = new Properties();
        propertyFiles.forEach(file -> {
            FileInputStream input = null;
            try {
                input = new FileInputStream(file);
                tmpProperty.load(input);
                input.close();
            } catch (IOException e) {
                throw new PropertyFileUsageException("IOException occurred while loading Property file in the specified path " + file);
            }
            properties.putAll(tmpProperty);
        });

        // Update maven property
        updateMavenProperties(properties, envConfiguration);

        // Load language
        loadLanguageBundle(properties.getProperty("language"));
        return properties;
    }

    /**
     * Get the value of any property in the system
     *
     * @param key : The key - which you want to get the value
     * @return : The value of key
     */
    public static String getPropertyValue(ConfigProperties key) {
        if (Objects.isNull(properties.getProperty(key.name().toLowerCase()))) {
            throw new PropertyFileUsageException("Property name - " + key + " is not found. Please check the config.properties");
        }
        return properties.getProperty(key.name().toLowerCase());
    }

    public static String getValue(String key) {
        String keyValue = null;
        try {
            // Lấy giá trị từ file đã Set
            keyValue = properties.getProperty(key);
            return LanguageUtils.convertCharset_ISO_8859_1_To_UTF8(keyValue);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return keyValue;
    }

    /**
     * Update env and property
     *
     * @param property
     * @param configObjects
     */
    private static void updateMavenProperties(Properties property, JSONObject configObjects) {
        Properties properties = System.getProperties();
        String lang = properties.getProperty("viLanguage", null);
        String testVersion = properties.getProperty("viVer", null);

        if (Objects.nonNull(lang) && !lang.isEmpty()) property.setProperty("language", lang);
        if (Objects.nonNull(testVersion) && !testVersion.isEmpty())
            property.setProperty("testingVersion", testVersion);

        String envName = properties.getProperty("viEnv", "sit");
        JSONObject configJSON = configObjects.getJSONObject("config");

        // Config for Project
        if (((JSONObject) configJSON.get("env")).keySet().contains(envName.toLowerCase())) {
            JSONObject envConfig = (JSONObject) configJSON.getJSONObject("env").get(envName.toLowerCase());
            FrameworkConst.USERNAME = envConfig.getString("userName");
            FrameworkConst.PASSWORD = envConfig.getString("password");
            FrameworkConst.API_FE_HOST = envConfig.getString("apiFeURL");
            FrameworkConst.MARKET_HOST = envConfig.getString("marketURL");
            FrameworkConst.BASE_URL = String.format("https://%s", FrameworkConst.MARKET_HOST);
            FrameworkConst.BASE_TOKEN = envConfig.getString("baseToken");
            FrameworkConst.DATABASE_CONNECT_CONFIG = (Boolean) envConfig.get("db_verification");
            FrameworkConst.EXE_ENV = envName.toUpperCase();
        } else Log.console("Don't have the viEnv Key - Please check again!!!");


        // Database Config
        FrameworkConst.DATABASE_CONNECT_LIST.clear();
        JSONObject databaseJSON = configObjects.getJSONObject("database");
        if (!FrameworkConst.DATABASE_CONNECT_CONFIG) return;
        JSONArray databaseEnvList = (JSONArray) databaseJSON.getJSONObject("env").get(envName.toLowerCase());
        databaseEnvList.toList().forEach(d -> {
            HashMap tmpDB = (HashMap) d;
            DatabaseType type = DatabaseType.valueOf(String.valueOf(tmpDB.get("type")).toUpperCase());
            FrameworkConst.DATABASE_CONNECT_LIST.add(DatabaseInfo.builder()
                    .url(String.valueOf(tmpDB.get("url")))
                    .name(String.valueOf(tmpDB.get("name")))
                    .userName(String.valueOf(tmpDB.get("username")))
                    .password(String.valueOf(tmpDB.get("password")))
                    .configPath(String.valueOf(tmpDB.get("config")))
                    .type(type).build());
        });
    }
}
