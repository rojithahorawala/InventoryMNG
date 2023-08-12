package com.example.inventoryapplication.server;

import com.example.inventoryapplication.log.ItemLog;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ServerThread implements Runnable {

    public String clientName;
    protected Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public ServerThread(Socket socket, String clientName) throws IOException {
        this.socket = socket;
        this.clientName = clientName;
        out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        out.flush();
        in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            out.writeObject(Server.logDatabase);
            out.flush();
            System.out.println("Synchronized server database to client database.");
            while (true) {
                Object receivedData = in.readObject();
                if (receivedData instanceof String && receivedData.toString().equals("!quit")) {
                    break;
                } else if(receivedData instanceof Object[] && ((Object[])receivedData)[0].equals("!setlog")) {
                    ItemLog receivedLog = (ItemLog)((Object[])receivedData)[1];
                    System.out.println(clientName + " is setting log " + receivedLog.getID() + " to the database.");
                    Server.logDatabase.put(receivedLog.getID(), receivedLog);
                    broadcastToAllOthers(receivedData);
                } else if(receivedData instanceof String && receivedData.toString().contains("!deletelog")) {
                    String logID = receivedData.toString().replace("!deletelog", "");
                    System.out.println(clientName + " is deleting log " + logID + " from the database.");
                    Server.logDatabase.remove(logID);
                    broadcastToAllOthers(receivedData);
                } else if(receivedData instanceof String && receivedData.toString().contains("!getlogdatabase")) {
                    System.out.println(clientName + " is getting the log database.");
                    out.writeObject(Server.logDatabase);
                }
            }
            socket.close();
        } catch (EOFException | SocketException ignored) {
        } catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        Server.connectedClients.remove(this);
        System.out.println(clientName + " disconnected from the server.");
    }

    protected static void broadcastToAll(Object o) throws IOException {
        for (ServerThread serverThread : Server.connectedClients) {
            serverThread.out.writeObject(o);
            serverThread.out.flush();
        }
    }

    private void broadcastToAllOthers(Object o) throws IOException {
        for (ServerThread serverThread : Server.connectedClients) {
            if (serverThread != this) {
                serverThread.out.writeObject(o);
                serverThread.out.flush();
            }
        }
        System.out.println(clientName + " is sending object " + o.getClass().getSimpleName() + " to all other clients.");
    }
}
