package com.company;

import java.io.*;
import java.net.Socket;
import java.util.Date;


public class Handler implements Runnable {
    private Server server;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private String userName;
    private Player player;
    private ObjectOutputStream objectOutputStream;
    private DataOutputStream dataOutputStream;

    public Handler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public synchronized void run() {
        try {
            userName = reader.readLine();
            while (server.checkUserName(userName)) {
                objectOutputStream.writeObject(null);
                userName = reader.readLine();
            }

            server.addUserName(userName);

            player = server.setRoll(userName);

            objectOutputStream.writeObject(player);


            while (true) {
                if (reader.readLine().equals("ready"))
                    break;
            }
            server.addPlayerHandler(player, this);

            sendMessage("Waiting to anther join...");
            if (server.getPlayerHandler().size() == 2) {
                synchronized (server.getPlayerHandler()) {
                    server.getPlayerHandler().notifyAll();

                }

            } else {

                try {
                    synchronized (server.getPlayerHandler()) {
                        server.getPlayerHandler().wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

//
//
            startGame();

        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Sends a message to the client.
     */
    public void sendMessage(String message) {
        writer.println(message);
    }

    public void sendObject(Object object) {
        try {
            objectOutputStream.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void startGame() {

//        try {
        sendMessage("Start game");
        sendMessage("Users:");
//        System.out.println(server.getUserNamesString());
//            objectOutputStream.writeObject(server.getUserNamesString());
//            dataOutputStream.writeUTF(server.getUserNamesString());
        sendMessage("Introduction night");
//        System.out.println(player.getRoll());
        sendMessage("your roll" + player.getRoll());
//
//
//            server.introductionNight(this);
        sendMessage("Start chat:");
        startChat();
        sendMessage("End chat");
        sendMessage("Night");


    }


    public void startChat() {
        String serverMessage;
        String clientMessage;
        try {
            do {
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                if (clientMessage.equals("End")) {
                    sendMessage("End");
                    break;
                }

                server.broadcast(serverMessage, this);

                if (clientMessage.equals("exit")) {
                    server.removePlayer(player);
                    socket.close();
                    serverMessage = userName + " has quitted.";
                    server.broadcast(serverMessage, this);
                }

            } while (true);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Player getPlayer() {
        return player;
    }

    public String readMessage() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
