package com.suzu.jiramanager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suzu.constants.ContentTypeManager;
import com.suzu.constants.FrameworkConst;
import com.suzu.jiramanager.models.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;

public class JiraCreateIssues {

    public HashMap<String, Object> createJiraIssues(String issueType, String issueName) throws JsonProcessingException {
        Map<String, Object> headersMap = new HashMap<>();
        headersMap.put("Accept", ContentTypeManager.APPLICATION_JSON.getValue());
        headersMap.put("Content-Type", ContentTypeManager.APPLICATION_JSON.getValue());
        headersMap.put("Authorization", "Basic " + JiraKey.JIRA_TOKEN.getValue());

        IssueType issuetype = new IssueType();
        issuetype.setId(issueType);
        Fields fields = Fields.builder()
                .components(List.of(new Component()))
                .issuetype(issuetype)
                .priority(new Priority())
                .summary(issueName)
                .project(new Project())
                .reporter(new Reporter())
                .fixVersions(new ArrayList<>())
                .labels(new ArrayList<>())
                .build();

        JSONObject requestBody = new JSONObject();

        requestBody.put("fields", new JSONObject(new ObjectMapper().writeValueAsString(fields)));
        requestBody.put("update", new JSONObject());

        try {
            RequestSpecification requestSpecification = RestAssured.given()
                    .baseUri(FrameworkConst.JIRA_DOMAIN + JiraAPIPath.CREATE_ISSUE_URL)
                    .headers(headersMap)
                    .body(requestBody.toString());

            Response response = requestSpecification.post();
            if (response.statusCode() == HTTP_OK) {
                return response.jsonPath().get();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

}
