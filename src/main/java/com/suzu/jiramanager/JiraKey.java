package com.suzu.jiramanager;

public enum JiraKey {
    JIRA_TOKEN("dHVhbi5wbTRAc2hiLmNvbS52")
    , JIRA_EMAIL("vincent@gmail.com")
    , JIRA_ID("712020:6aca3cb2-2703-4ea2-b4e8-d95ac8353c09")
    , TASK("10002")
    , ISSUE("10014")
    , ID_PROJECT("10052");

    private String value;

    JiraKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
