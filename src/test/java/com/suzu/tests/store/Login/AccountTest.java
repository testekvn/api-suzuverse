package com.suzu.tests.store.Login;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.suzu.common.TestBase;
import com.suzu.controller.store.login.AccountModel;
import com.suzu.dataprovider.AccountDataProvider;
import com.suzu.constants.AuthorType;
import com.suzu.constants.CategoryType;
import com.suzu.constants.FrameworkAnnotation;
import com.suzu.services.squad4.login.AuthenServices;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AccountTest extends TestBase {
    AuthenServices authServices = new AuthenServices();

    @BeforeClass
    public void setUp() throws JsonProcessingException {
//        JiraCreateIssues jiraCreateIssues = new JiraCreateIssues();
//        HashMap<String, Object> data = jiraCreateIssues.createJiraIssues(JiraKey.ISSUE.getValue(), "Test Auto create issues");
    }

    @FrameworkAnnotation(author = {AuthorType.BGDuy}, category = {CategoryType.REGRESSION, CategoryType.SMOKE}, reviewer = {AuthorType.BGDuy})
    @Test(priority = 1, description = "Test login flow", dataProvider = "SUZU_STORE_ACCOUNT", dataProviderClass = AccountDataProvider.class)
    public void SUZU_Account_001(AccountModel accountModel) {
        System.out.println("Vincent - Test");
        Response response = authServices.getAccessToken(accountModel);

        authServices.verifyResponse(accountModel,response);

    }
}
