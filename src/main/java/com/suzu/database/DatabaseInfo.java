package com.suzu.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DatabaseInfo {
    DatabaseType type;
    String name;
    String url;
    String userName;
    String password;
    String configPath;
}
