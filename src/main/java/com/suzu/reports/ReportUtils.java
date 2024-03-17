package com.suzu.reports;


import com.suzu.constants.FrameworkConst;
import com.suzu.utils.DateUtils;
import com.suzu.utils.PlatformManager;

public final class ReportUtils {

    private ReportUtils() {
    }

    public static String createExtentReportPath(String reportName) {
        if (FrameworkConst.OVERRIDE_REPORTS.trim().equalsIgnoreCase(FrameworkConst.NO)) {
            return FrameworkConst.EXTENT_REPORT_FOLDER_PATH + "/" + PlatformManager.getPlatformName() + "_" + DateUtils.getCurrentDate() + "_"
                    + FrameworkConst.EXTENT_REPORT_NAME + reportName + ".html";
        } else {
            return FrameworkConst.EXTENT_REPORT_FOLDER_PATH + "/" + FrameworkConst.EXTENT_REPORT_NAME + reportName + ".html";
        }
    }

    public static String createJsonExtentObserverPath(String reportName) {
        if (FrameworkConst.OVERRIDE_REPORTS.trim().equalsIgnoreCase(FrameworkConst.NO)) {
            return FrameworkConst.EXTENT_REPORT_FOLDER_PATH + "/" + PlatformManager.getOSInfo() + "_" + DateUtils.getCurrentDate() + "_"
                    + FrameworkConst.EXTENT_REPORT_NAME + reportName + ".json";
        } else {
            return FrameworkConst.EXTENT_REPORT_FOLDER_PATH + "/" + FrameworkConst.EXTENT_REPORT_NAME + reportName + ".json";
        }
    }
}

