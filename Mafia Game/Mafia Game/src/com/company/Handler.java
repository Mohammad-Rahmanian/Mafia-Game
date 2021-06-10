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
            thread = Thread.currentThread();
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
            shareData.addPlayer(player);
            server.addPlayerHandler(player, this);
            if (shareData.getPlayers().size() == server.getNumberOfPlayers()) {

                synchronized (shareData.getPlayers()) {
                    shareData.getPlayers().notifyAll();

                }
            } else {

                try {
                    synchronized (shareData.getPlayers()) {
                        shareData.getPlayers().wait();
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
            sendMessage("Start chat:");
            startChat();
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
