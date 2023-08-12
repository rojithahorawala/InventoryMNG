package com.example.inventoryapplication.networking;

import android.util.Log;

import com.example.inventoryapplication.log.ItemLog;
import com.example.inventoryapplication.log.LogManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class Client {

    private static Socket server;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private static Queue<Object> objectOutQueue = new LinkedList<>();
    private static final String DEFAULT_IP = "10.0.2.2"; //10.0.2.2 == localhost on PC
    private static final int DEFAULT_PORT = 9091;

    public static void connectToServer() {
        connectToServer(DEFAULT_IP, DEFAULT_PORT);
    }

    public static void connectToServer(String serverIp, Integer port) {
        if (!isConnected()) {
            new Thread(() -> {
                try {
                    server = new Socket(serverIp, port);
                    out = new ObjectOutputStream(new BufferedOutputStream(server.getOutputStream()));
                    out.flush();
                    in = new ObjectInputStream(new BufferedInputStream(server.getInputStream()));
                    Log.i("Network", "Connected to server at IP: " + serverIp + ":" + port);
                    new Thread(() -> {
                        while (isConnected()) {
                            Object objectToSend = objectOutQueue.poll();
                            if (objectToSend != null) {
                                try {
                                    out.writeObject(objectToSend);
                                    out.flush();
                                    if (objectToSend instanceof Object[]) {
                                        Log.i("Network", "Sent " + ((ItemLog)((Object[])objectToSend)[1]).getID() + " to server at IP: " + serverIp + ":" + port);
                                    } else {
                                        Log.i("Network", "Sent " + objectToSend.toString() + " to server at IP: " + serverIp + ":" + port);
                                    }
                                } catch (IOException e) {
                                    Log.e("Network", "Exception at IP: " + serverIp + ":" + port, e);
                                }
                            }
                        }
                    }).start();
                    try {
                        LogManager.localLogDatabase = (ConcurrentHashMap<String, ItemLog>)in.readObject();
                        Log.i("Network", "Synchronized local database with server database at IP: " + serverIp + ":" + port);
                        while (isConnected()) {
                            Object receivedData = in.readObject();
                            Log.i("Network", "Response from server at IP: " + serverIp + ":" + port + " : " + receivedData);
                            if (receivedData instanceof String && receivedData.toString().equals("!quit")) {
                                server.close();
                                Log.i("Network", "Closing connection with server at IP: " + serverIp + ":" + port);
                            } else if (receivedData instanceof Object[] && ((Object[]) receivedData)[0].equals("!setlog")) {
                                ItemLog receivedLog = (ItemLog) ((Object[]) receivedData)[1];
                                LogManager.createLog(receivedLog.getType(), receivedLog.getName(), receivedLog.getMinAmt(), receivedLog.getActualAmt(), true);
                            } else if (receivedData instanceof String && receivedData.toString().contains("!deletelog")) {
                                String logID = receivedData.toString().replace("!deletelog", "");
                                LogManager.deleteLog(logID, true);
                            }
                        }
                    } catch (EOFException ignored) {
                    } catch (IOException | ClassNotFoundException e) {
                        Log.e("Network", "Exception at IP: " + serverIp + ":" + port, e);
                        server.close();
                    }
                } catch (IOException e) {
                    Log.e("Network", "Failed to connect to server at IP: " + serverIp + ":" + port);
                }
            }).start();
        } else {
            Log.i("Network", "Already connected to server at IP: " + serverIp + ":" + port);
        }
    }

    public static void sendLog(ItemLog log) {
        if (isConnected()) objectOutQueue.add(new Object[]{"!setlog",log});
    }

    public static void deleteLog(String logID) {
        if (isConnected()) objectOutQueue.add("!deletelog" + logID);
    }

    public static void closeConnection() throws IOException {
        out.writeObject("!quit");
        server.close();
        Log.i("Network", "Closing connection with server");
    }

    public static boolean isConnected() {
        if (server == null) return false;
        return !server.isClosed();
    }
}
