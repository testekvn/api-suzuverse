package com.suzu.common;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.suzu.common.auth.AccountInfo;
import com.suzu.common.auth.Authentication;
import com.suzu.constants.FrameworkConst;
import com.suzu.database.DatabaseFactory;
import com.suzu.datadriven.BaseModel;
import com.suzu.reports.ExtentReportManager;
import com.suzu.utils.Log;
import com.suzu.utils.configloader.JsonUtils;
import com.suzu.utils.configloader.PropertyUtils;
import com.suzu.utils.testcase.TestCase;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONObject;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

@Listeners({TestListener.class})
public class TestBase extends TestSystem {

    protected TestBase() {
        Log.info("TestBase: beforeSuite");
        JSONObject envConfig = JsonUtils.loadAllConfiguration();
        PropertyUtils.loadAllProperties(envConfig);
    }

    @BeforeSuite(alwaysRun = true)
    protected void beforeSuite() {
        ExtentReportManager.initReports(null, FrameworkConst.APP_VERSION, FrameworkConst.APP_ENV, FrameworkConst.EXE_ENV, true);
        // Init Account
        AccountInfo accountInfo = AccountInfo.builder().userName(FrameworkConst.USERNAME).password(FrameworkConst.PASSWORD).build();
        APIBase.ACCOUNT_MAP.put(accountInfo.getUserName(), accountInfo);

        // TO-DO: Login function
        new Authentication().getAccessToken(accountInfo, FrameworkConst.BASE_TOKEN, Strings.EMPTY, "1");

        // Connect databases
        if (FrameworkConst.DATABASE_CONNECT_CONFIG) {
            FrameworkConst.DATABASE_CONNECT_LIST.forEach(databaseInfo -> {
                DatabaseFactory.initDatabaseConnection(databaseInfo.getType(), databaseInfo.getName(), databaseInfo.getUserName(), databaseInfo.getPassword(), databaseInfo.getUrl());
            });
        }
    }


    @BeforeClass
    protected void setUp() throws JsonProcessingException {
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Method method, Object[] data, ITestResult iTestResult) {
        String tcName = method.getName();
        String desc = iTestResult.getMethod().getDescription();
        String id = "";
        var testcase = new TestCase();
        if (data.length > 0) {
            iTestResult.setParameters(new Object[]{});
            id = ((BaseModel) data[0]).getTestId().getValue().toString();
            tcName = tcName.concat("___").concat(id);
            desc = ((BaseModel) data[0]).getTestDesc().getValue().toString();
        }
        testcase.setId(id);
        testcase.setName(tcName);
        testcase.setDesc(desc);
        setTestCase(testcase);

        //setTestName(TESTCASE);
        addInvocation(iTestResult);
    }

    @AfterMethod(alwaysRun = true)
    protected void afterMethod(ITestResult result) {
    }

    @AfterClass(alwaysRun = true)
    protected void afterClass() {
    }

    @AfterSuite(alwaysRun = true)
    protected void afterSuite() {
    }

    @BeforeTest(alwaysRun = true)
    public void beforeTest(ITestContext context) {
        String testName = context.getName();
        ExtentReportManager.initReports(testName, FrameworkConst.APP_VERSION, FrameworkConst.APP_ENV, FrameworkConst.EXE_ENV, false);
    }

    @AfterTest(alwaysRun = true)
    public void afterTest(ITestContext context) {

    }

    public void addInvocation(ITestResult tr) {
        tr.setAttribute("invocation", tr.getMethod().getParameterInvocationCount());
        AtomicReference<String> dataId = new AtomicReference<>(tr.getTestName() != null ? tr.getTestName() : tr.getMethod().getConstructorOrMethod().getName());
        if (tr.getParameters().length > 0) {
            Arrays.stream(tr.getParameters()).forEach(o -> {
                try {
                    BaseModel model = (BaseModel) o;
                    String temp = String.valueOf(model.getTestId().getValue());
                    if (!temp.isEmpty()) dataId.set(temp);
                    return;
                } catch (Exception e) {
                    // Exception casting
                }
            });
        }
        tr.setAttribute("dataId", dataId.get());
    }
}
