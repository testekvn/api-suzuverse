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
public class Project {
    private String id = JiraKey.ID_PROJECT.getValue();
}
