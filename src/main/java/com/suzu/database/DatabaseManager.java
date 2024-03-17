package com.suzu.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DatabaseManager {

    public static String DBXDB = "DBTEST";

    static Map<String, ThreadLocal<SessionFactory>> databaseManagerMap = new HashMap<>();

    public static SessionFactory getSessionFactory(String dbName) {
        ThreadLocal<SessionFactory> threadLocalDriver = databaseManagerMap.get(dbName.trim().toUpperCase());
        return threadLocalDriver.get();
    }

    public static void setSessionFactory(SessionFactory session, String dbName) {
        if (Objects.nonNull(session)) {
            ThreadLocal<SessionFactory> threadLocalDriver = new ThreadLocal<>();
            threadLocalDriver.set(session);
            databaseManagerMap.put(dbName.trim().toUpperCase(), threadLocalDriver);
        }

    }

    public static void shutdown(String dbName) {
        DatabaseManager.getSessionFactory(dbName).close();
    }

    public static Session openDBSession(String dbName) {
        Session session = DatabaseManager.getSessionFactory(dbName).openSession();
        session.clear();
        return session;
    }

    public static void closeDBSession(Session session) {
        if (Objects.nonNull(session)) session.close();
    }
}
