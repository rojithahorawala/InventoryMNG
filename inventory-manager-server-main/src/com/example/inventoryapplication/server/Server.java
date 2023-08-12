package com.example.inventoryapplication.server;

import com.example.inventoryapplication.log.ItemLog;

import java.io.*;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 9091;

    public static ConcurrentHashMap<String, ItemLog> logDatabase = new ConcurrentHashMap<>();
    public static final CopyOnWriteArrayList<ServerThread> connectedClients = new CopyOnWriteArrayList<>();
    private static final ExecutorService pool = Executors.newCachedThreadPool();

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        ServerSocket listener = new ServerSocket(PORT);
        System.out.println("Server is waiting for client connection.");
        new Thread(() -> {
            while (!listener.isClosed()) {
                try {
                    ServerThread serverThread = new ServerThread(listener.accept(), "Client" + (connectedClients.size() + 1));
                    System.out.println(serverThread.clientName + " connected to the server.");
                    connectedClients.add(serverThread);
                    pool.execute(serverThread);
                } catch (SocketException ignore) {
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        boolean isQuit = false;
        while (!isQuit) {
            String input = sc.nextLine();
            if (input.equalsIgnoreCase("!save")) {
                saveLogDatabase();
                System.out.println("Successfully saved the log Database");
            } else if (input.equalsIgnoreCase("!load")) {
                System.out.println("Successfully loaded the log Database");
                loadLogDatabase();
            } else if (input.equalsIgnoreCase("!quit")) {
                saveLogDatabase();
                listener.close();
                isQuit = true;
                ServerThread.broadcastToAll("!quit");
                System.out.println("Successfully terminated server");
            }
        }
    }

    public static void saveLogDatabase() {
        try {
            try(ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File("LogDatabase.ser"), false)))) {
                out.writeObject(logDatabase);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadLogDatabase() {
        try {
            try(ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File("LogDatabase.ser"))))) {
                logDatabase = (ConcurrentHashMap<String, ItemLog>)in.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
