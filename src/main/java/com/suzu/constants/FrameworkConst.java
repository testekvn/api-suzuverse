package com.suzu.constants;

import com.suzu.database.DatabaseInfo;
import com.suzu.utils.configloader.PropertyUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FrameworkConst {
    public static final String PROJECT_PATH = System.getProperty("user.dir");
    public static final String TEST_RESOURCES_DIR = PROJECT_PATH + File.separator + "src/test/resources";
    public static final String ENV_CONFIGURATION_PATH =
            TEST_RESOURCES_DIR + File.separator + "config" + File.separator + "config/database.json";
    public static final String TEST_DATA_SHEET = "TEST_DATA";
    public static final String NUMBER_PATTERN = "(^-?[0-9,\\.]+$)|^\\(?[0-9,\\.]+\\)$";
    public static final String OS_SYSTEM = PropertyUtils.getValue("os_system");
    public static final String REPORT_TITLE = PropertyUtils.getValue("reportTitle");
    public static final String TESTING_VERSION = PropertyUtils.getValue("testingVersion");
    public static final String EXTENT_REPORT_NAME = PropertyUtils.getValue("extentReportName");
    public static final String EXTENT_REPORT_FOLDER = PropertyUtils.getValue("extentReportFolder");
    public static final String AUTHOR = PropertyUtils.getValue("author");
    public static final String TFS_LINK = "tfslink";
    public static final String OVERRIDE_REPORTS = PropertyUtils.getValue("override_reports");
    public static final String EXTENT_REPORT_FOLDER_PATH = PROJECT_PATH + File.separator + EXTENT_REPORT_FOLDER;
    public static final String EXTENT_REPORT_FILE_NAME = EXTENT_REPORT_NAME + ".html";
    public static String EXTENT_REPORT_FILE_PATH = EXTENT_REPORT_FOLDER_PATH + "/" + EXTENT_REPORT_FILE_NAME;
    //Zip file for Report folder
//  public static final String Zipped_ExtentReports_Folder_Name = EXTENT_REPORT_FOLDER + ".zip";
    public static final String YES = "yes";
    public static final String NO = "no";
    public static final String BOLD_START = "<b>";
    public static final String BOLD_END = "</b>";
    public static final String FAIL = BOLD_START + "FAIL" + BOLD_END;
    public static final String PASS = BOLD_START + "PASS" + BOLD_END;
    /* ICONS - START */
    public static final String ICON_OS_WINDOWS = "<i class='fa fa-windows' ></i>";
    public static final String ICON_OS_MAC = "<i class='fa fa-apple' ></i>";
    public static final String ICON_OS_LINUX = "<i class='fa fa-linux' ></i>";
    private static final String EXTENT_REPORT_PATH = PROJECT_PATH + File.separator + "extent-test-report";
    private static final String SCREEN_RECORDING_PATH = PROJECT_PATH + File.separator + "screen-recordings";
    public static String LOG_LEVEL = "DEBUG";
    public static String USERNAME = "";
    public static String PASSWORD = "";
    public static String MARKET_HOST = "";
    public static String API_FE_HOST = "";
    public static String BASE_URL = "";
    public static String BASE_TOKEN = "";
    public static String JIRA_DOMAIN = "";
    public static boolean DATABASE_CONNECT_CONFIG = false;
    public static List<DatabaseInfo> DATABASE_CONNECT_LIST = new ArrayList<>();
    //endregion


    /* PROJECT CONFIG */
    public static String SEPARATE_KEY = "%MS%";
    public static String CONFIG_COL = "CONFIG";
    public static String DATA_ID_COL = "DATA_ID";
    public static String APP_VERSION = PropertyUtils.getValue("viVer");
    public static String APP_ENV = "API";
    public static String EXE_ENV = "SIT";

    public static String DATA_NOT_FOUND ="NOT_FOUND";

    private FrameworkConst() {
    }

    public static String getExtentReportPath() {
        if (PropertyUtils.getPropertyValue(ConfigProperties.OVERRIDE_REPORTS).equalsIgnoreCase("yes")) {
            return EXTENT_REPORT_PATH + File.separator + "index.html";
        } else {
            return EXTENT_REPORT_PATH + File.separator + getCurrentDateTime() + File.separator + "index.html";
        }
    }

    public static String getScreenRecordingsPath() {
        File screenRecordingsDir = new File(SCREEN_RECORDING_PATH);
        if (!screenRecordingsDir.exists()) {
            screenRecordingsDir.mkdir();
        }
        return SCREEN_RECORDING_PATH;
    }

    private static String getCurrentDateTime() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd-HH_mm_ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        return dateTimeFormatter.format(localDateTime);
    }
}
