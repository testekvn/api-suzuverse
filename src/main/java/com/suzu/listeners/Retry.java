package com.suzu.listeners;

import com.suzu.constants.ConfigProperties;
import com.suzu.utils.configloader.PropertyUtils;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class Retry implements IRetryAnalyzer {

    private static int status = ITestResult.CREATED;
    private final int maxRetry = Integer.parseInt(PropertyUtils.getPropertyValue(ConfigProperties.RETRY_COUNT));
    private int count = 0;

    public static int getRetryStatus() {
        return status;
    }

    @Override
    public boolean retry(ITestResult result) {
        boolean value = false;
        if (PropertyUtils.getPropertyValue(ConfigProperties.RETRY_FAILED_TESTS).equalsIgnoreCase("yes")) {
            value = count < maxRetry;
            count++;
        }
        return value;
    }
}
