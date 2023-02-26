package com.kgromov.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataSourceSettings {
    private String uri;
    private String dbName;
    private String user;
    private String password;

    // jdbc:mysql://localhost:3306/weather_archive
    public String connectionString() {
        return uri + '/' + dbName;
    }
}
