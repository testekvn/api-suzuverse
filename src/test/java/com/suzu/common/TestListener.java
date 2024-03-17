package com.suzu.common;

import com.aventstack.extentreports.Status;
import com.suzu.constants.*;
import com.suzu.listeners.Retry;
import com.suzu.reports.ExtentReportManager;
import com.suzu.reports.ExtentTestManager;
import com.suzu.utils.IconUtils;
import com.suzu.utils.Log;
import org.apache.logging.log4j.util.Strings;
import org.testng.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/*
 * Purpose: Implement the testing listener
 * Datetime:
 */
public class TestListener implements ITestListener, ISuiteListener, IInvokedMethodListener, IConfigurationListener {
    static int totalTCs;
    static int passedTCs;
    static int skippedTCs;
    static int failedTCs;

    private final String separateItem = "\n---------------------------------------------------------------";

    public String getTestName(ITestResult result) {
        return result.getTestName() != null ? result.getTestName() : result.getMethod().getConstructorOrMethod().getName();
    }

    public String getTestDescription(ITestResult result) {
        return TestSystem.getTestCase().getDesc();
        //return result.getMethod().getDescription() != null ? result.getMethod().getDescription() : Strings.EMPTY;
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        // System.out.println("beforeInvocation ------" + method.getTestMethod().getMethodName() + " -- " + testResult.getTestName());
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        //System.out.println("afterInvocation ------" + method.getTestMethod().getMethodName() + " -- " + testResult.getTestName());
    }

    @Override
    public void onStart(ISuite iSuite) {
        Log.info(String.format("%s\nTestListener: TESTING FOR TEST SUITE: %s%s", separateItem, iSuite.getName(), separateItem));

//    ExtentReportManager.initReports(null, null, null, null, false);
    }

    @Override
    public void onFinish(ISuite iSuite) {
        Log.info(String.format("\nTestListener: FINISH TESTING FOR TEST SUITE: %s %s", iSuite.getName(), separateItem));
    }

    public AuthorType[] getAuthorType(ITestResult iTestResult) {
        if (iTestResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(FrameworkAnnotation.class) == null) {
            return null;
        }
        return iTestResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(FrameworkAnnotation.class).author();
    }

    public AuthorType[] getModifier(ITestResult iTestResult) {
        if (iTestResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(FrameworkAnnotation.class) == null) {
            return null;
        }
        return iTestResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(FrameworkAnnotation.class).modifier();
    }

    public CategoryType[] getCategoryType(ITestResult iTestResult) {
        if (iTestResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(FrameworkAnnotation.class) == null) {
            return null;
        }
        return iTestResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(FrameworkAnnotation.class).category();
    }

    public Map<String, String> getTSTCLevel(ITestResult iTestResult) {
        if (iTestResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(FrameworkAnnotation.class) == null) {
            return null;
        }
        Map<String, String> tcLevelMap = new HashMap<>();
        TCLevel[] tcLevels = iTestResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(FrameworkAnnotation.class).tcLevel();
        Arrays.stream(Objects.requireNonNull(tcLevels)).forEach(item -> {
            if (item.toString().contains("TC_")) tcLevelMap.put("tcLevel", item.toString());
            if (item.toString().contains("TS_")) tcLevelMap.put("tsLevel", item.toString());
        });

        return tcLevelMap;
    }

    @Override
    public void onTestStart(ITestResult iTestResult) {
        Log.info(String.format("%s\nTestListener: START TC:  %s", separateItem, getTestName(iTestResult)));
        totalTCs = increaseTestNum(totalTCs);

        ExtentTestManager.unload();
        addTestToExtentReport(iTestResult);
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        Log.info(String.format("\nTestListener: COMPLETED TC: %s - PASS %s", getTestName(iTestResult), separateItem));
        updateRetryTestName(iTestResult);

        passedTCs = increaseTestNum(passedTCs);

        //AllureManager.saveTextLog("Test case: " + getTestName(iTestResult) + " - PASS");
        //ExtentReports log operation for passed tests.
        ExtentReportManager.logMessage(Status.PASS, "Test case: " + getTestName(iTestResult) + " - PASS");
        ExtentReportManager.unloadTest();
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        Log.info(String.format("\nTestListener: COMPLETED TC: %s - FAIL %s", getTestName(iTestResult), separateItem));
        updateRetryTestName(iTestResult);

        failedTCs = increaseTestNum(failedTCs);

        //Allure report screenshot file and log
        Log.error("FAILED !! Screenshot for test case: " + getTestName(iTestResult));

        //AllureManager.takeScreenshotToAttachOnAllureReport();

        if (ExtentTestManager.getExtentTest() == null) {
            addTestToExtentReport(iTestResult);
        }

        //Extent report screenshot file and log
        ExtentReportManager.logMessage(Status.FAIL, "Test case: " + getTestName(iTestResult) + " - FAIL");
        if (Objects.nonNull(iTestResult.getThrowable())) {
            Log.error(iTestResult.getThrowable());
            ExtentReportManager.logMessage(Status.FAIL, iTestResult.getThrowable());
        }
        ExtentReportManager.unloadTest();
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        Log.info(String.format("\nTestListener: COMPLETED TC: %s - SKIP %s", getTestName(iTestResult), separateItem));

        skippedTCs = increaseTestNum(skippedTCs);
        updateRetryTestName(iTestResult);

        if (ExtentTestManager.getExtentTest() == null) {
            addTestToExtentReport(iTestResult);
        }

        ExtentReportManager.logMessage(Status.SKIP, "Test case: " + getTestName(iTestResult) + " - SKIP");
        ExtentReportManager.unloadTest();
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        if (ExtentTestManager.getExtentTest() == null) {
            addTestToExtentReport(iTestResult);
        }
        Log.error("Test failed but it is in defined success ratio " + getTestName(iTestResult));
        ExtentReportManager.logMessage("Test failed but it is in defined success ratio " + getTestName(iTestResult));
        ExtentReportManager.unloadTest();
    }

    @Override
    public void onConfigurationSuccess(ITestResult tr) {
        String className = tr.getTestClass().getName();
        ExtentReportManager.logMessage(Status.WARNING, "Configuration: " + getTestName(tr) + " - PASS");
        ExtentReportManager.unloadTest();
        ExtentReportManager.removeTest(tr.getName() + " " + className.substring(className.lastIndexOf(".") + 1));
        flushReport(tr);
    }

    @Override
    public void onConfigurationFailure(ITestResult tr) {
        ExtentReportManager.logMessage(Status.WARNING, "Configuration: " + getTestName(tr) + " - FAIL");
        if (Objects.nonNull(tr.getThrowable())) {
            Log.error(tr.getThrowable());
            ExtentReportManager.logMessage(Status.WARNING, tr.getThrowable());
        }
        ExtentReportManager.unloadTest();
        flushReport(tr);
    }

    @Override
    public void onConfigurationSkip(ITestResult tr) {
        ExtentReportManager.logMessage(Status.WARNING, "Configuration: " + getTestName(tr) + " - SKIP");
        ExtentReportManager.unloadTest();
        flushReport(tr);
    }

    @Override
    public void beforeConfiguration(ITestResult tr) {
        String className = tr.getTestClass().getName();
        ExtentReportManager.createTest(tr.getName() + " " + className.substring(className.lastIndexOf(".") + 1));
        ExtentReportManager.logMessage(Status.WARNING, "START - Configuration: " + getTestName(tr));
    }

    private void updateRetryTestName(ITestResult iTestResult) {
        String oldName = getTestName(iTestResult);
        if (Objects.nonNull(ExtentTestManager.getExtentTest())) {
            String extendName = ExtentTestManager.getExtentTest().getModel().getName();
            String newName = extendName.replace(oldName, iTestResult.getName());
            ExtentTestManager.getExtentTest().getModel().setName(newName);
        }
    }

    private int increaseTestNum(int current) {
        int retryStatus = Retry.getRetryStatus();
        if (Objects.equals(retryStatus, ITestResult.CREATED) || Objects.equals(retryStatus, ITestResult.SUCCESS))
            return current + 1;
        return current;
    }

    private void addTestToExtentReport(ITestResult iTestResult) {
        AuthorType[] author = getAuthorType(iTestResult);
        String des = (author.length > 0 ? (author[0] + " - ") : Strings.EMPTY) + getTestDescription(iTestResult);
        iTestResult.setAttribute("author", author.length > 0 ? author[0].toString() : "");

        AuthorType[] modifiers = getModifier(iTestResult);
        iTestResult.setAttribute("reviser", modifiers.length > 0 ? modifiers[0].toString() : "");
        String dataId = iTestResult.getName();
        if (iTestResult.getAttributeNames().contains("invocation")) {
            dataId = iTestResult.getAttributeNames().contains("dataId") ? iTestResult.getAttribute("dataId").toString() : iTestResult.getName();
            des = String.format("%s </br> ID: %s - Invocation %s", des, iTestResult.getMethod().getMethodName(), iTestResult.getAttribute("invocation"));
        }

        Map<String, String> tcLevel = getTSTCLevel(iTestResult);
        iTestResult.setAttribute("tcLevel", tcLevel.getOrDefault("tcLevel", ""));
        iTestResult.setAttribute("tsLevel", tcLevel.getOrDefault("tsLevel", ""));

//    ExtentReportManager.createTest(iTestResult.getName(), des);
        ExtentReportManager.createTest(dataId, des);
        ExtentReportManager.addAuthors(author);
        String nameTestClass = iTestResult.getTestClass().getName();
        ExtentReportManager.addCategory(nameTestClass.substring(nameTestClass.lastIndexOf(".") + 1));
        ExtentReportManager.addTFSLink(getTFSLink(iTestResult));
        ExtentReportManager.info(FrameworkConst.BOLD_START + IconUtils.getOSIcon() + " " + FrameworkConst.BOLD_END);
    }

    public String getTFSLink(ITestResult iTestResult) {
        if (iTestResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(TFSLink.class) == null) {
            return null;
        }
        return iTestResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(TFSLink.class).value();
    }

    private void flushReport(ITestResult iTestResult) {
        String method = iTestResult.getMethod().getConstructorOrMethod().getName();
        if (method.contains("beforeSuite") || method.contains("afterTest")) ExtentReportManager.flushReports();
    }
}
