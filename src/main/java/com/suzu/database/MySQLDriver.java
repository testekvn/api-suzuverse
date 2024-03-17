package com.suzu.database;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class MySQLDriver extends DatabaseDriver {
    @Override
    public SessionFactory initSessionFactory(String dbURL, String userName, String password) {
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.url", dbURL);
        configuration.setProperty("hibernate.connection.username", userName);
        configuration.setProperty("hibernate.connection.password", password);
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        configuration.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        return buildSessionFactory(configuration);
    }
}
