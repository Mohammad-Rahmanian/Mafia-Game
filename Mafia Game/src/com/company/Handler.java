package com.company;

import java.io.*;
import java.net.Socket;


public class Handler implements Runnable {
    private Server server;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private String userName;
    private Player player;
    private ObjectOutputStream objectOutputStream;
    private DataOutputStream dataOutputStream;
    private Thread thread;
    private ShareData shareData;

    public Handler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        shareData = server.getShareData();
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
            while (shareData.checkUserName(userName)) {
                objectOutputStream.writeObject(null);
                userName = reader.readLine();
            }

            shareData.addUserName(userName);


            player = GameManger.setRoll(userName);

            objectOutputStream.writeObject(player);


            while (true) {
                if (reader.readLine().equals("ready"))
                    break;
            }
            shareData.addPlayerHandler(player, this);

            sendMessage("Waiting to anther join...");
            if (shareData.getPlayerHandler().size() == 2) {
                synchronized (shareData.getPlayerHandler()) {
                    shareData.getPlayerHandler().notifyAll();

                }

            } else {

                try {
                    synchronized (shareData.getPlayerHandler()) {
                        shareData.getPlayerHandler().wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            writer.println("Start game");

//            startGame();

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
        thread = Thread.currentThread();
        sendMessage("Start chat:");
        startChat();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendMessage("End chat");

        synchronized (server.getThread()) {
            server.getThread().notify();
        }
        synchronized (thread) {
            try {
                thread.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        sendMessage("Night");


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
//                    shareData.removePlayer(player);
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

    public Thread getThread() {
        return thread;
    }

    public String getUserName() {
        return userName;
    }

    public void sendString(String string) {
        try {
            dataOutputStream.writeUTF(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPlayer(Player player) {
        try {
            Player player1 = player;
            System.out.println(player1.toString());
            objectOutputStream.writeObject(player1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
