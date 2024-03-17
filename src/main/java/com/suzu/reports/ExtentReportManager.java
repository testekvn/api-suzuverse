package com.suzu.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.JsonFormatter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import com.suzu.constants.AuthorType;
import com.suzu.constants.CategoryType;
import com.suzu.constants.FrameworkConst;
import com.suzu.constants.HTTPMethod;
import com.suzu.utils.Log;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.QueryableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.SpecificationQuerier;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class ExtentReportManager {
    static String currentReportName;
    static String screenshotPath;
    private static ExtentReports extentReports;

    public static void initReports(String reportName, String appVersion, String appName, String excEnv, boolean isUpdate) {
        if (Objects.isNull(extentReports) || isUpdate) {
            if (Objects.isNull(extentReports))
                extentReports = new ExtentReports();

            String TESTING_VERSION;
            if (Objects.nonNull(excEnv) && Objects.nonNull(appName) && Objects.nonNull(appVersion))
                TESTING_VERSION = String.format("%s_%s_%s", excEnv.toUpperCase(), appName.toUpperCase(), appVersion);
            else TESTING_VERSION = FrameworkConst.TESTING_VERSION;

            reportName = (Objects.nonNull(reportName) && !reportName.isEmpty()) ? "_" + reportName : "";

            currentReportName = ReportUtils.createExtentReportPath(reportName);
            ExtentSparkReporter spark = new ExtentSparkReporter(currentReportName);
            String jsonFilePath = ReportUtils.createJsonExtentObserverPath(reportName);
            JsonFormatter json = new JsonFormatter(jsonFilePath);
            extentReports.attachReporter(json, spark);

            configSpark(spark, TESTING_VERSION);

            if (!reportName.isEmpty() || isUpdate) {
                extentReports.setSystemInfo("Framework Name", FrameworkConst.REPORT_TITLE);
                extentReports.setSystemInfo("Author", FrameworkConst.AUTHOR);
                extentReports.setSystemInfo("Testing Version", TESTING_VERSION);
                extentReports.setSystemInfo("Testing Device", FrameworkConst.APP_ENV);
            }
        }

        screenshotPath = FrameworkConst.EXTENT_REPORT_FOLDER_PATH + File.separator + "Screenshot";
        File file = new File(screenshotPath);
        if (!file.exists()) {
            file.mkdir();
            Log.info("Screenshot will be saved at " + file);
        }
    }

    /**
     * Config spark for reading the testing report
     */
    private static void configSpark(ExtentSparkReporter spark, String testVersion) {
        spark.config().setOfflineMode(true);
        spark.config().setTheme(Theme.STANDARD);
        spark.config().setDocumentTitle(FrameworkConst.REPORT_TITLE + " Test version " + testVersion);
        spark.config().setReportName(FrameworkConst.REPORT_TITLE + " Test version " + testVersion);
        spark.config().setEncoding("UTF-8");
        spark.viewConfigurer().viewOrder().as(
                new ViewName[]{ViewName.DASHBOARD, ViewName.TEST, ViewName.CATEGORY, ViewName.DEVICE, ViewName.EXCEPTION, ViewName.AUTHOR,
                        ViewName.LOG}).apply();
    }

    public static void flushReports() {
        if (Objects.nonNull(extentReports)) {
            extentReports.flush();
            updateContent(currentReportName);
        }
        ExtentTestManager.unload();
        extentReports = null;
    }

    /**
     * Create an extent test then attach to the final report
     *
     * @param testCaseName : The name of tc
     */
    public static void createTest(String testCaseName) {
        if (Objects.nonNull(extentReports))
            ExtentTestManager.setExtentTest(extentReports.createTest(testCaseName, null));
    }

    public static void createTest(String testCaseName, String description) {
        String testName = testCaseName +
                String.format("<br/> <p style='font-size: 0.75em'>%s</p>", description);
        if (Objects.nonNull(extentReports)) ExtentTestManager.setExtentTest(extentReports.createTest(testName, null));
    }

    public static void unloadTest() {
        if (Objects.nonNull(ExtentTestManager.getExtentTest())) ExtentTestManager.unload();
    }

    public static void removeTest(String testCaseName) {
        unloadTest();
        if (Objects.nonNull(extentReports))
            extentReports.removeTest(testCaseName);
    }

    synchronized public static void addAuthors(AuthorType[] authors) {
        if (authors == null) {
            ExtentTestManager.getExtentTest().assignAuthor("AUTHOR");
        } else {
            for (AuthorType author : authors) {
                ExtentTestManager.getExtentTest().assignAuthor(author.toString());
            }
        }

    }

    // public static void addCategories(String[] categories) {
    synchronized public static void addTestingType(CategoryType[] categories) {
        if (categories == null) {
            ExtentTestManager.getExtentTest().assignCategory("REGRESSION");
        } else {
            Arrays.stream(categories).forEach(c -> addCategory(c.toString()));
        }
    }

    public static void addTFSLink(String tfsLink) {
        if (Objects.nonNull(tfsLink) && !tfsLink.isEmpty()) {
            String[] tmp = tfsLink.split(",");
            for (String link : tmp)
                ExtentTestManager.getExtentTest().info(
                        MarkupHelper.createLabel(
                                "This TC has been FAILED, please check <a href=\"" + FrameworkConst.TFS_LINK + link + "\">TFS Link</a>",
                                ExtentColor.AMBER));
        }
    }

    public static void addNode(String message, String nodeTitle) {
        if (ExtentTestManager.getExtentTest() != null) {
            ExtentTest extentTest = ExtentTestManager.getExtentTest().createNode(nodeTitle);
            extentTest.log(Status.INFO, message);
        }
    }

    synchronized public static void addCategory(String cateName) {
        ExtentTestManager.getExtentTest().assignCategory(cateName);
    }

    public static void logMessage(String message) {
        if (ExtentTestManager.getExtentTest() != null) ExtentTestManager.getExtentTest().log(Status.INFO, message);
    }

    public static void logMessage(Status status, String message) {
        if (ExtentTestManager.getExtentTest() != null) ExtentTestManager.getExtentTest().log(status, message);
    }

    public static void logMessage(Status status, Object message) {
        if (ExtentTestManager.getExtentTest() != null)
            ExtentTestManager.getExtentTest().log(status, (Throwable) message);
    }

    public static void pass(String message) {
        if (ExtentTestManager.getExtentTest() != null) ExtentTestManager.getExtentTest().pass(message);
    }

    public static void pass(Markup message) {
        if (ExtentTestManager.getExtentTest() != null) ExtentTestManager.getExtentTest().pass(message);
    }

    public static void fail(String message) {
        if (ExtentTestManager.getExtentTest() != null) ExtentTestManager.getExtentTest().fail(message);
    }

    public static void fail(Object message) {
        if (ExtentTestManager.getExtentTest() != null) ExtentTestManager.getExtentTest().fail((String) message);
    }

    public static void fail(Markup message) {
        if (ExtentTestManager.getExtentTest() != null) ExtentTestManager.getExtentTest().fail(message);
    }

    public static void skip(String message) {
        if (ExtentTestManager.getExtentTest() != null) ExtentTestManager.getExtentTest().skip(message);
    }

    public static void skip(Markup message) {
        if (ExtentTestManager.getExtentTest() != null) ExtentTestManager.getExtentTest().skip(message);
    }

    public static void info(Markup message) {
        if (ExtentTestManager.getExtentTest() != null) ExtentTestManager.getExtentTest().info(message);
    }

    public static void info(String message) {
        if (ExtentTestManager.getExtentTest() != null) ExtentTestManager.getExtentTest().info(message);
    }

    public static void warning(String message) {
        if (ExtentTestManager.getExtentTest() != null) ExtentTestManager.getExtentTest().log(Status.WARNING, message);
    }

    public static void logResponse(Response response) {
        logResponseInReport(response);
    }

    public static void logRequest(RequestSpecification requestSpecification, HTTPMethod method) {
        QueryableRequestSpecification query = SpecificationQuerier.query(requestSpecification);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(method.name()).append(" : ").append(query.getURI()).append("\nHeaders:");
        for (Header header : query.getHeaders()) {
            stringBuilder.append("\n\t").append(header.getName()).append(":").append(header.getValue()).append("\n");
        }
        String reqBody = query.getBody();
        if (Objects.nonNull(reqBody))
            stringBuilder.append("Body\n").append(reqBody);

        logRequestInReport(stringBuilder.toString());
        if (FrameworkConst.LOG_LEVEL.equalsIgnoreCase("Debug")) {
            requestSpecification.log().all();
        }
    }

    public static void logRequestInReport(String request) {
        if (Objects.nonNull(ExtentTestManager.getExtentTest())) {
            //ExtentTestManager.getExtentTest().log(Status.INFO, MarkupHelper.createLabel("API REQUEST", ExtentColor.ORANGE));
            ExtentTestManager.getExtentTest().log(Status.INFO, MarkupHelper.createCodeBlock(request));
        }
    }

    public static void logResponseInReport(Response response) {
        if (FrameworkConst.LOG_LEVEL.equalsIgnoreCase("Debug")) {
            System.out.println("=== HTTP Code: " + response.statusCode());
            System.out.println("SDebug - Response: \n" + response.asPrettyString());
        }

        if (Objects.nonNull(ExtentTestManager.getExtentTest())) {
//            ExtentTestManager.getExtentTest().log(Status.INFO, MarkupHelper.createLabel("API RESPONSE:", ExtentColor.ORANGE));
            ExtentTestManager.getExtentTest().log(Status.INFO, "API RESPONSE: HTTP Code :   " + response.statusCode());
            ExtentTestManager.getExtentTest().log(Status.INFO, MarkupHelper.createCodeBlock(response.asPrettyString()));
        }
    }

    public static void logHeaders(List<Header> headerList) {
        String[][] headers = headerList.stream().map(header -> new String[]{header.getName(), header.getValue()})
                .toArray(String[][]::new);
        ExtentTestManager.getExtentTest().info(MarkupHelper.createTable(headers));
    }

    private static void updateContent(String reportName) {
        String defaultLogo = "spark/logo.png";
        String newLogo = "";

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(reportName), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String val;
            while ((val = br.readLine()) != null) {
                if (val.isEmpty()) continue;
                if (val.contains(defaultLogo)) val = val.replace(defaultLogo, newLogo);
                if (val.contains("class=\"nav-logo\""))
                    val = val.replace("class=\"nav-logo\"", " class=\"nav-logo\" style=\"padding:0 0\"");
                if (val.contains("</html>"))
                    val = val.replace("</html>",
                            "<style>.test-wrapper .test-content .test-content-detail .detail-head .info{height: unset;}</style></html>");
                stringBuilder.append(val).append("\n");
            }
            br.close();

            File f = new File(reportName);
            FileUtils.writeStringToFile(f, stringBuilder.toString(), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
