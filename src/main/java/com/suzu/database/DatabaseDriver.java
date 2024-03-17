package com.suzu.database;

import com.suzu.utils.Log;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public abstract class DatabaseDriver {
    private Session session;

    public abstract SessionFactory initSessionFactory(String dbURL, String userName, String password);

    SessionFactory buildSessionFactory(Configuration configuration) {
        try {
            if (configuration != null) {
                // Set default configuration
                configuration.setProperty("show_sql", String.valueOf(true));
                configuration.setProperty("hibernate.temp.use_jdbc_metadata_defaults", String.valueOf(false));
                //this.configuration.configure(fileConfig);
                return configuration.buildSessionFactory();
            }
        } catch (Throwable ex) {
            Log.error("Unable to connect DB, Please check again!!!");
            //throw new ExceptionInInitializerError(ex);
        }
        return null;
    }


}
