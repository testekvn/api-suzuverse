package com.suzu.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suzu.common.auth.AccountInfo;
import com.suzu.constants.ContentTypeManager;
import com.suzu.constants.FailureHandling;
import com.suzu.constants.FrameworkConst;
import com.suzu.constants.HTTPMethod;
import com.suzu.datadriven.DataModel;
import com.suzu.reports.AllureManager;
import com.suzu.reports.ExtentReportManager;
import com.suzu.utils.Log;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.asserts.SoftAssert;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.suzu.constants.FrameworkConst.*;
import static com.suzu.constants.HTTPMethod.*;

public class APIBase {
    public static Map<String, AccountInfo> ACCOUNT_MAP = new HashMap<>();
    private static SoftAssert softAssert = new SoftAssert();

    public static void stopSoftAssertAll() {
        softAssert.assertAll();
    }

    /**
     * Assert Element Objects.
     * Support 3 type of Failure Handling
     */
    public static void assertTrueCondition(boolean isResult, FailureHandling failureHandling, @Nullable String errMsg, Object... noScreenshot) {
        if (Objects.isNull(errMsg) || errMsg.isEmpty()) {
            errMsg = "Verify compare condition (TRUE): ";
        }

        try {
            if (!isResult) {
                Log.info(String.format("%s -> VERIFY : %s", errMsg, isResult));
                //AllureManager.saveTextLog(String.format("%s -> VERIFY : %s", errMsg, isResult));
            }
            switch (failureHandling) {
                case STOP_ON_FAILURE:
                    if (!isResult) {
                        ExtentReportManager.fail(String.format("%s -> VERIFY : %s", errMsg, FAIL));
                    }
                    Assert.assertTrue(isResult);
                    ExtentReportManager.pass(String.format("%s -> VERIFY : %s", errMsg, PASS));
                    break;
                case CONTINUE_ON_FAILURE:
                    softAssert.assertTrue(isResult);
                    if (!isResult) {
                        String softMsg = "SOFT ASSERT: Assert TRUE object: FAILED";

                        Reporter.getCurrentTestResult().setStatus(ITestResult.FAILURE);
                        ExtentReportManager.fail(String.format("%s -> VERIFY : %s", errMsg, FAIL));
                    } else {
                        ExtentReportManager.pass(String.format("%s -> VERIFY : %s", errMsg, PASS));
                    }
                    break;
                default:
                    break;
            }
        } finally {
        }
    }

    /**
     * Assert Fail
     */
    public static void assertFalseCondition(boolean isResult, FailureHandling failureHandling, String errMsg, Object... noScreenshot) {
        String apiLog = "";
        if (Objects.isNull(errMsg) || errMsg.isEmpty()) {
            errMsg = "Verify compare condition (FAIL)";
        } else if (errMsg.contains(SEPARATE_KEY)) {
            String[] errs = errMsg.split(SEPARATE_KEY);
            errMsg = errs[0];
            apiLog = errs[1];
        }
        try {
            if (isResult) {
                Log.info(String.format("%s -> VERIFY : FALSE", errMsg));
                ExtentReportManager.logMessage(errMsg);
                //AllureManager.saveTextLog(String.format("%s -> VERIFY : %s", errMsg + "\n" + apiLog, !isResult));
            }
            switch (failureHandling) {
                case STOP_ON_FAILURE:
                    if (isResult) {
                        ExtentReportManager.fail(String.format("%s -> VERIFY : %s", errMsg, FAIL));
                        ExtentReportManager.addNode(apiLog, "API Response");
//                        ExtentReportManager.logMessage("API Log: " + apiLog);
                    }
                    Assert.assertFalse(isResult);
                    ExtentReportManager.pass(String.format("%s -> VERIFY : %s", errMsg, PASS));
                    break;
                case CONTINUE_ON_FAILURE:
                    softAssert.assertFalse(isResult);
                    if (isResult) {
                        String softMsg = "SOFT ASSERT: Verify FALSE object: FAILED";

                        Reporter.getCurrentTestResult().setStatus(ITestResult.FAILURE);
                        ExtentReportManager.fail(String.format("%s -> VERIFY : %s", errMsg, FAIL));
//                        ExtentReportManager.logMessage("API Log: " + apiLog);
                        //ExtentReportManager.addNode(apiLog, "API Response");

                    } else {
                        ExtentReportManager.pass(String.format("%s -> VERIFY : %s", errMsg, FrameworkConst.PASS));
                    }
                    break;
                case OPTIONAL:
                    break;
            }
        } finally {
        }
    }

    /**
     * Assert Equal
     */
    public static void assertEqualCondition(Object actual, Object expected, FailureHandling failureHandling, String errMsg, Object... noScreenshot) {
        boolean isResult = Objects.equals(actual, expected);

        if (Objects.isNull(errMsg) || errMsg.isEmpty()) {
            errMsg = String.format("Verify equal object: ");
        }

        errMsg = String.format("%s - Actual: %s ; Expected: %s", errMsg, actual.toString(), expected.toString());

        try {
            if (!isResult) {
                Log.info(String.format("%s -> VERIFY : %s", errMsg, isResult));
                AllureManager.saveTextLog(String.format("%s -> VERIFY : %s", errMsg, isResult));
            }

            switch (failureHandling) {
                case STOP_ON_FAILURE:
                    if (!isResult) {
                        ExtentReportManager.fail(String.format("%s -> VERIFY : %s", errMsg, FAIL));
                    }
                    Assert.assertEquals(actual, expected);
                    ExtentReportManager.pass(String.format("%s -> VERIFY : %s", errMsg, PASS));
                    break;
                case CONTINUE_ON_FAILURE:
                    softAssert.assertEquals(actual, expected);
                    if (!isResult) {
                        String softMsg = "SOFT ASSERT: Verify the result: FAILED";
                        Reporter.getCurrentTestResult().setStatus(ITestResult.FAILURE);
                        ExtentReportManager.fail(String.format("%s -> VERIFY : %s", errMsg, FAIL));
                    } else {
                        ExtentReportManager.pass(String.format("%s -> VERIFY : %s", errMsg, PASS));
                    }
                    break;
            }
        } finally {
        }
    }

    /**
     * Assert Equal
     */
    public static <T> void assertThatCondition(T actual, Matcher<? super T> matcher, FailureHandling failureHandling, String errMsg) {
        boolean isPass = true;
        try {
            MatcherAssert.assertThat(actual, matcher);
        } catch (AssertionError e) {
            isPass = false;
            Log.error(e.getMessage());
            Reporter.getCurrentTestResult().setStatus(ITestResult.FAILURE);
            ExtentReportManager.fail(String.format("%s -> \nVERIFY : %s", e.getMessage(), FAIL));
        }
//        ExtentReportManager.pass(String.format("%s -> \nVERIFY : %s", errMsg, PASS));

        switch (failureHandling) {
            case STOP_ON_FAILURE:
                if (!isPass) {
                    ExtentReportManager.fail(String.format("%s -> VERIFY : %s", errMsg, FAIL));
                    Assert.fail();
                }
                ExtentReportManager.pass(String.format("%s -> VERIFY : %s", errMsg, PASS));
                break;
            case CONTINUE_ON_FAILURE:
                if (!isPass) {
                    String softMsg = "SOFT ASSERT: Verify the result: FAILED";
                    Reporter.getCurrentTestResult().setStatus(ITestResult.FAILURE);
                    ExtentReportManager.fail(String.format("%s -> VERIFY : %s", errMsg, FAIL));
                } else {
                    ExtentReportManager.pass(String.format("%s -> VERIFY : %s", errMsg, PASS));
                }
                break;
        }
    }

    // Verify HTTP Code & Time
    public void verifyHTTPStatusCodeAndTime(Response response, String expCode, long expTime) {
        String actCode = String.valueOf(response.statusCode());
        assertEqualCondition(actCode, expCode, FailureHandling.CONTINUE_ON_FAILURE, "Verify HTTP Code");

        long actResponseTime = response.getTime();
        assertTrueCondition(actResponseTime < expTime, FailureHandling.CONTINUE_ON_FAILURE, String.format("Verify HTTP Response Time (ms): Act: %s less than Exp:%s", actResponseTime, expTime));
    }

    // Verify HTTP Code & Time
    public void verifyHTTPStatusCodeAndTime(Response response, DataModel codeModel, DataModel timeModel) {
        verifyHTTPStatusCodeAndTime(response, codeModel.getValue().toString(), Long.parseLong(String.valueOf(timeModel.getValue().toString())));
    }

    //region Rest API
    public String getRequestBodyFromDataModel(DataModel dataModel) {
        HashMap<String, Object> bodies = (HashMap<String, Object>) dataModel.getValue();
        return new JSONObject(bodies).toString();
    }
    //endregion

    public RequestSpecification createDefaultRequest(String apiPath, boolean isSecure, String... uris) {
        String endPoint = uris.length > 0 ? uris[0] : FrameworkConst.BASE_URL;
        endPoint += apiPath;
        RequestSpecification requestSpec = RestAssured.given().baseUri(endPoint);
        return isSecure ? requestSpec.relaxedHTTPSValidation() : requestSpec;
    }

    /**
     * Generate the default header
     *
     * @param contentType : The content type for request
     * @return A header map, including App-Key, App-Secret, Integrity, Content-Type
     */
    public Map<String, Object> createDefaultHeader(AccountInfo accountInfo, ContentTypeManager contentType) {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("Content-Type", contentType.getValue());
        return headerMap;
    }

    /**
     * Generate the default header
     *
     * @param contentType : The content type for request
     * @return A header map, including App-Key, App-Secret, Integrity, Content-Type
     */
    public Map<String, Object> createDefaultHeaderForLogin(AccountInfo accountInfo, ContentTypeManager contentType, boolean includeToken) {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("Content-Type", contentType.getValue());
        return headerMap;
    }

    //endregion

    public Map<String, Object> createDefaultHeaderForLogin2(AccountInfo accountInfo, ContentTypeManager contentType, boolean includeToken) {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("X-Kony-App-Key", "c011d3e38abb5f70dca7f3359d1c73df");
        headerMap.put("X-Kony-App-Secret", "9d0db0003747e1477c368a813907bc4c");
        headerMap.put("Content-Type", contentType.getValue());
        return headerMap;
    }

    /**
     * Call Rest API
     *
     * @param method  : The method type
     * @param request : The request
     * @return API response
     */
    public Response callRestAPI(HTTPMethod method, RequestSpecification request) {
        Response response = null;
        switch (method) {
            case POST:
                ExtentReportManager.logRequest(request, POST);
                response = request.post();
                ExtentReportManager.logResponse(response);
                break;
            case PUT:
                ExtentReportManager.logRequest(request, PUT);
                response = request.put();
                ExtentReportManager.logResponse(response);
                break;
            case GET:
                ExtentReportManager.logRequest(request, GET);
                response = request.get();
                ExtentReportManager.logResponse(response);
                break;
            case DELETE:
                ExtentReportManager.logRequest(request, DELETE);
                response = request.delete();
                ExtentReportManager.logResponse(response);
                break;
            case PATCH:
                ExtentReportManager.logRequest(request, PATCH);
                response = request.patch();
                ExtentReportManager.logResponse(response);
                break;
        }
        return response;
    }

    //region Extract data from Response
    private <T> T extractDataFromResponse(Response response, String jsonPath) {
        return response.jsonPath().get(jsonPath);
    }

    public Object extractResponseDataToString(Response response, String jsonPath, Object defaultVal) {
        var res = extractDataFromResponse(response, jsonPath);
        if (Objects.nonNull(res)) {
            return String.valueOf(res);
        }
        return defaultVal;
    }

    public JSONObject extractResponseDataToJSON(Response response, String jsonPath) {
        JSONObject jsonObject = new JSONObject(response.prettyPrint());
        String[] pathArr = jsonPath.split("\\.");
        for (String curPath : pathArr) {
            if (jsonObject != null && jsonObject.keySet().contains(curPath)) {
                jsonObject = jsonObject.getJSONObject(curPath);
            } else {
                return null;
            }
        }
        return jsonObject;
    }

    /**
     * Convert the response data to an expected object
     */
    public <T> T convertResponseToObject(Response response, Class<T> object) {
        return convertStringToObject(response.prettyPrint(), object);
    }

    /**
     * Convert the data (String) to an expected object
     */
    public <T> T convertStringToObject(String data, Class<T> object) {
        T expObj;
        try {
            expObj = new ObjectMapper().readValue(data, object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return expObj;
    }

    /**
     * Convert the data (String) to an expected object
     */
    public <T> T convertRequestDataToObject(Object data, Class<T> object) {
        return new ObjectMapper().convertValue(data, object);
    }
    // endregion

}
