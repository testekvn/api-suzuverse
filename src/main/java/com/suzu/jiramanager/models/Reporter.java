package com.suzu.jiramanager.models;

import com.suzu.jiramanager.JiraKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Reporter {
    public String id = JiraKey.JIRA_ID.getValue();
}
