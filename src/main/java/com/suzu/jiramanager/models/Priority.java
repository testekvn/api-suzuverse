package com.suzu.jiramanager.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Priority {
    private String id = "3";
    private String name = "Medium";
    private String iconUrl = "https://testek.atlassian.net/images/icons/priorities/medium.svg";
}
