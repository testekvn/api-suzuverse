package com.suzu.jiramanager.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Fields {
    private Project project;
    private IssueType issuetype;
    private String summary;
    private Reporter reporter;
    private Priority priority;
    private List<Object> labels;
    @Builder.Default
    private String customField_10014 = "CTECH-321";
    private List<Object> fixVersions;
    private List<Component> components;
}




