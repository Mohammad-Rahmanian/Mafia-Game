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
        thread = Thread.currentThread();
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
                sendMessage("Error");
                userName = reader.readLine();
            }

            shareData.addUserName(userName);
            sendMessage("If you ready say : ready");


            while (true) {
                if (reader.readLine().equals("ready"))
                    break;
            }
            GameManger.addReadyUser(userName);

            if (GameManger.getReadyUsers().size() == server.getNumberOfPlayers()) {

                synchronized (GameManger.getReadyUsers()) {
                    GameManger.getReadyUsers().notifyAll();

                }
            } else {

                try {
                    synchronized (GameManger.getReadyUsers()) {
                        GameManger.getReadyUsers().wait();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            startGame();
        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    public void startGame() {

//        try {
            sendMessage("Start game");

//            objectOutputStream.writeObject(shareData);
            synchronized (server.getThread()) {
                server.getThread().notify();
            }
            goToSleep();
        System.out.println("salaaaaaaam");

////            server.introductionNight(this);



//            sendMessage("Start chat:");
//            startChat();
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            sendMessage("End chat");
//
//            synchronized (server.getThread()) {
//                server.getThread().notify();
//            }
//            synchronized (thread) {
//                try {
//                    thread.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
////        sendMessage("Night");
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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

    public void goToSleep() {
        try {
            synchronized (thread) {
                thread.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
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

    public void setPlayer(Player player) {
        this.player = player;
    }
}
