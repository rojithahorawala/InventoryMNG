package com.example.inventoryapplication.log;

import com.example.inventoryapplication.ApplicationContext;
import com.example.inventoryapplication.networking.Client;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public class LogManager {
    public static ConcurrentHashMap<String, ItemLog> localLogDatabase = new ConcurrentHashMap<>();
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss aa", Locale.getDefault());
    private static ItemLog itemLog;
    private static String oldItemLogID;
    private static LogManagerListener logManagerListener = new LogManagerListener() {
        public void onCreatedLog(ItemLog createdLog) { }
        public void onModifiedLog(ItemLog oldLog, ItemLog modifiedLog) { }
        public void onDeletedLog(ItemLog deletedLog) { }

    };

    public interface LogManagerListener {
        void onCreatedLog(ItemLog createdLog);
        void onModifiedLog(ItemLog oldLog, ItemLog modifiedLog);
        void onDeletedLog(ItemLog deletedLog);
    }

    public static boolean createLog(String type, String name, int min_amt, int actual_amt) {
        if (!Client.isConnected()) {
            ApplicationContext.showToast("Failed: Could not create log due to connection failure.");
            return false;
        } else if (localLogDatabase.containsKey(type + "_" + name)) {
            ApplicationContext.showToast("Failed: Another log with the same type and name already exists.");
            return false;
        }
        ItemLog itemLog = new ItemLog();
        itemLog.setType(type);
        itemLog.setName(name);
        itemLog.setMinAmt(min_amt);
        itemLog.setActualAmt(actual_amt);
        itemLog.setCreationDate(FORMATTER.format(new Date()));
        itemLog.setLastUpdateDate(FORMATTER.format(new Date()));
        localLogDatabase.put(itemLog.getID(), itemLog);
        Client.sendLog(itemLog);
        logManagerListener.onCreatedLog(itemLog);
        ApplicationContext.showToast("Success: Successfully created log");
        return true;
    }

    public static boolean createLog(String type, String name, int min_amt, int actual_amt, boolean isFromServer) {
        if (!isFromServer) {
            if (!Client.isConnected()) {
                ApplicationContext.showToast("Failed: Could not create log due to connection failure.");
                return false;
            } else if (localLogDatabase.containsKey(type + "_" + name)) {
                ApplicationContext.showToast("Failed: Another log with the same type and name already exists.");
                return false;
            }
        }
        ItemLog itemLog = new ItemLog();
        itemLog.setType(type);
        itemLog.setName(name);
        itemLog.setMinAmt(min_amt);
        itemLog.setActualAmt(actual_amt);
        itemLog.setCreationDate(FORMATTER.format(new Date()));
        itemLog.setLastUpdateDate(FORMATTER.format(new Date()));
        localLogDatabase.put(itemLog.getID(), itemLog);
        if (!isFromServer) {
            Client.sendLog(itemLog);
            ApplicationContext.showToast("Success: Successfully created log");
        }
        logManagerListener.onCreatedLog(itemLog);
        return true;
    }

    public static boolean deleteLog(String logID) {
        if (!Client.isConnected()) {
            ApplicationContext.showToast("Failed: Could not delete log due to connection failure.");
            return false;
        }
        ItemLog logToBeDeleted = localLogDatabase.get(logID);
        localLogDatabase.remove(logID);
        Client.deleteLog(logID);
        logManagerListener.onDeletedLog(logToBeDeleted);
        ApplicationContext.showToast("Success: Successfully deleted log");
        return true;
    }

    public static boolean deleteLog(String logID, boolean isFromServer) {
        if (!Client.isConnected() && !isFromServer) {
            ApplicationContext.showToast("Failed: Could not delete log due to connection failure.");
            return false;
        }
        ItemLog logToBeDeleted = localLogDatabase.get(logID);
        localLogDatabase.remove(logID);
        if (!isFromServer) {
            Client.deleteLog(logID);
            ApplicationContext.showToast("Success: Successfully deleted log");
        }
        logManagerListener.onDeletedLog(logToBeDeleted);
        return true;
    }

    public static ItemLog getLog(String LogID) {
        return localLogDatabase.get(LogID);
    }

    /*Because of a very weird mystical bug, when sending previously sent logs to the server,
    the server thinks the object's values were unchanged when it receives it even though it was actually modified,
    the only way to fix it is by creating a new log and sending that through.
    This affects both the overhaulLog and commitModification methods*/
    public static boolean overhaulLog(String logID, String type, String name, int min_amt, int actual_amt) {
        if (!Client.isConnected()) {
            ApplicationContext.showToast("Failed: Could not modify log due to connection failure.");
            return false;
        }
        LogManager.itemLog = getLog(logID);
        localLogDatabase.remove(logID);
        Client.deleteLog(logID);
        ItemLog copy = new ItemLog();
        copy.setType(type);
        copy.setName(name);
        copy.setMinAmt(min_amt);
        copy.setActualAmt(actual_amt);
        copy.setCreationDate(itemLog.getCreationDate());
        copy.setLastUpdateDate(FORMATTER.format(new Date()));
        localLogDatabase.put(copy.getID(), copy);
        Client.sendLog(copy);
        logManagerListener.onModifiedLog(itemLog, copy);
        ApplicationContext.showToast("Success: Successfully modified log");
        return true;
    }

    public static void beginModification(String logID) {
        LogManager.itemLog = getLog(logID);
        oldItemLogID = logID;
    }

    public static void modifyType(String type) {
        itemLog.setType(type);
    }

    public static void modifyName(String name) {
        itemLog.setType(name);
    }

    public static void modifyMinAmt(int n) {
        itemLog.setMinAmt(n);
    }

    public static void modifyActualAmt(int n) {
        itemLog.setActualAmt(n);
    }

    public static boolean commitModification() {
        if (!Client.isConnected()) {
            ApplicationContext.showToast("Failed: Could not modify log due to connection failure.");
            return false;
        }
        localLogDatabase.remove(oldItemLogID);
        Client.deleteLog(oldItemLogID);
        ItemLog copy = new ItemLog();
        copy.setType(itemLog.getType());
        copy.setName(itemLog.getName());
        copy.setMinAmt(itemLog.getMinAmt());
        copy.setActualAmt(itemLog.getActualAmt());
        copy.setCreationDate(itemLog.getCreationDate());
        copy.setLastUpdateDate(FORMATTER.format(new Date()));
        localLogDatabase.put(copy.getID(), copy);
        Client.sendLog(copy);
        logManagerListener.onModifiedLog(itemLog, copy);
        ApplicationContext.showToast("Success: Successfully modified log");
        return true;
    }

    public static void setLogManagerListener(LogManagerListener logManagerListener) {
        LogManager.logManagerListener = logManagerListener;
    }
}
