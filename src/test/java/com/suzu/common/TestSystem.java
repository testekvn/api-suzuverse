package com.suzu.common;

import com.suzu.utils.testcase.TestCase;
import org.testng.ITest;

import java.util.Objects;

public class TestSystem implements ITest {
    static ThreadLocal<TestCase> testCase = new ThreadLocal<>();
//    private ThreadLocal<String> testName = new ThreadLocal<>();

    public static TestCase getTestCase() {
        if (Objects.isNull(testCase.get())) {
            setTestCase(new TestCase());
        }
        return testCase.get();
    }

    public static void setTestCase(TestCase tc) {
        testCase.set(tc);
    }

    @Override
    public String getTestName() {
        return getTestCase().getName();
    }

}
