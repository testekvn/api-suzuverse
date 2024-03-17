package com.suzu.utils.configloader;

import com.jayway.jsonpath.JsonPath;
import com.suzu.constants.FrameworkConst;
import com.suzu.datadriven.DataModel;
import com.suzu.exceptions.InvalidPathException;
import com.suzu.utils.Log;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public final class JsonUtils {
    public static String getValue(String key) {
        try {
            return JsonPath.read(new File(FrameworkConst.ENV_CONFIGURATION_PATH), key);
        } catch (IOException e) {
            throw new InvalidPathException("Check the config.json");
        }
    }

    /**
     * Read data from Json file
     *
     * @param filePath : Json file Path
     */
    public static String readDataFromJsonFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            Log.error(String.format("Exception occurred - Reading json file:%s\n%s ", filePath, e.getMessage()));
            throw new RuntimeException(e);
        }
        return "";
    }

    /**
     * Load all env configuration
     *
     * @return : A JSONObject
     */
    public static synchronized JSONObject loadAllConfiguration() {
        // Add all property
        JSONObject envConfigJSON = new JSONObject();

        String configPath = "src/test/resources/config/env.json";
        JSONObject configObject = new JSONObject(readDataFromJsonFile(configPath));
        envConfigJSON.put("config", configObject);


        String dbPath = "src/test/resources/config/database.json";
        JSONObject dbObject = new JSONObject(readDataFromJsonFile(dbPath));
        envConfigJSON.put("database", dbObject);
        return envConfigJSON;
    }

    /**
     * Read data from XLS file then return Data Object
     *
     * @param tcName : TC Name
     * @return Data Provider Object
     */
    public List<Hashtable<String, Object>> vinGetDataDrivenFromJSON(String jsonFilePath, String tcName) {
        List<Hashtable<String, Object>> dataListMap = new ArrayList<>();

        try {
            String content = readDataFromJsonFile(jsonFilePath);
            JSONObject jsonObject = new JSONObject(content);
            var maps = (List<HashMap<String, Object>>) jsonObject.toMap().get(tcName);
            maps.forEach(data -> {
                Hashtable<String, Object> rowDataMap = new Hashtable<>();
                data.forEach((key, value) -> rowDataMap.put(key, DataModel.builder().devName(key).value(value).build()));
                dataListMap.add(rowDataMap);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataListMap;
    }

}
