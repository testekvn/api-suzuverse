package com.suzu.database;

import com.suzu.utils.Log;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.transform.Transformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBQueryCommand {

    /**
     * Get data  from the database and parse to your object
     *
     * @param queryCommand     :Your query command
     * @param targetClassName: Your mapping object
     * @return : Data Object
     */
    public static <R> R selectSingleNativeQueryDatabase(String dbName, String queryCommand, Class<R> targetClassName) {
        R result = null;
        Session session = null;
        try {
            session = DatabaseManager.openDBSession(dbName);
            result = (R) session.createNativeQuery(queryCommand)
                    .setResultTransformer(Transformers.aliasToBean(targetClassName)).getSingleResult();
        } catch (Exception e) {
            Log.error("VDebug: Error - Get Data From Database.." + e);
        } finally {
            DatabaseManager.closeDBSession(session);
        }
        return result;
    }

    /**
     * Get data  from the database and parse to your object
     *
     * @param queryCommand     :Your query command
     * @param targetClassName: Your mapping object
     * @return : Data Object
     */
    public static <R> List<R> selectListNativeQueryDatabase(String dbName, String queryCommand, Class<R> targetClassName) {
        List<R> result = new ArrayList<>();
        Session session = null;
        try {
            session = DatabaseManager.openDBSession(dbName);
            result = session.createNativeQuery(queryCommand).setResultTransformer(Transformers.aliasToBean(targetClassName)).getResultList();
        } catch (Exception e) {
            Log.error("VDebug: Error - - Get Data From Database.." + e);
        } finally {
            DatabaseManager.closeDBSession(session);
        }
        return result;
    }

    /**
     * Get data  from the database and parse to your object
     *
     * @param queryCommand :Your query command
     * @return : A list of Hashmap data
     */
    public static List<HashMap<String, Object>> selectListNativeQueryDatabase(String dbName, String queryCommand) {
        List result = new ArrayList();
        Session session = null;
        try {
            session = DatabaseManager.openDBSession(dbName);
            result = session.createNativeQuery(queryCommand).setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE).getResultList();
        } catch (Exception e) {
            Log.error("VDebug: Error - Get Data From Database.." + e);
        } finally {
            DatabaseManager.closeDBSession(session);
        }
        return result;
    }

    /**
     * Get data from the database and parse to your object
     *
     * @param queryCommand :Your query command
     * @return : Hashmap data
     */
    public static HashMap<String, Object> selectSingleNativeQueryDatabase(String dbName, String queryCommand) {
        HashMap<String, Object> result = null;
        Session session = null;
        try {
            session = DatabaseManager.openDBSession(dbName);
            result = (HashMap<String, Object>) session.createNativeQuery(queryCommand)
                    .setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE).getSingleResult();
        } catch (Exception e) {
            Log.error("VDebug: Error - Get Data From Database.." + e);
        } finally {
            DatabaseManager.closeDBSession(session);
        }
        return result;
    }

    /**
     * Update your data to the database
     *
     * @param queryCommand : Your command
     * @return : a string result (Success or Error)
     */
    public static String updateNativeQueryDatabase(String dbName, String queryCommand) {
        return updateDatabase(dbName, queryCommand, "UPDATE");
    }

    /**
     * Insert your data to the database
     *
     * @param queryCommand : Your command
     * @return : a string result (Success or Error)
     */
    public static String insertNativeQueryDatabase(String dbName, String queryCommand) {
        return updateDatabase(dbName, queryCommand, "INSERT");
    }

    /**
     * Delete your data to the database
     *
     * @param queryCommand : Your command
     * @return : a string result (Success or Error)
     */
    public static String deleteNativeQueryDatabase(String dbName, String queryCommand) {
        return updateDatabase(dbName, queryCommand, "DELETE");
    }

    /**
     * Update your data to DB {@link #updateNativeQueryDatabase(String, String)}
     * {@link #insertNativeQueryDatabase(String, String)} {@link #deleteNativeQueryDatabase(String, String)}
     *
     * @param queryCommand
     * @param type
     * @return
     */
    private static String updateDatabase(String dbName, String queryCommand, String type) {
        Log.info(String.format("%s - command: %s", type.toUpperCase(), queryCommand));
        String result = "Success";
        Session session = null;
        try {
            session = DatabaseManager.openDBSession(dbName);
            session.clear();
            session.beginTransaction();
            Query query = session.createNativeQuery(queryCommand);
            query.executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            Log.error(String.format("VDebug: Error - %s Database..\n%s", type, e));
            result = "Error";
        } finally {
            DatabaseManager.closeDBSession(session);
        }
        return result;
    }

}
