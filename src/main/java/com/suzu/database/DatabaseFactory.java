package com.suzu.database;

import org.hibernate.SessionFactory;

import java.util.HashMap;
import java.util.Map;

public class DatabaseFactory {
    private static final Map<String, DatabaseDriver> databaseDriverMap;

    static {
        databaseDriverMap = new HashMap<>();
        databaseDriverMap.put(DatabaseType.MYSQL.name(), new MySQLDriver());
    }


    /**
     * Init Database Driver
     *
     * @param databaseType : Database Type
     * @param userName     : UserName
     * @param password     : Password
     * @param databaseURL  : Database URL
     */
    public static void initDatabaseConnection(DatabaseType databaseType, String dbName, String userName, String password, String databaseURL) {
        DatabaseDriver databaseDriver = databaseDriverMap.get(String.valueOf(databaseType).toUpperCase());
        SessionFactory sessionFactory = databaseDriver.initSessionFactory(databaseURL, userName, password);
        DatabaseManager.setSessionFactory(sessionFactory, dbName);
    }
}
