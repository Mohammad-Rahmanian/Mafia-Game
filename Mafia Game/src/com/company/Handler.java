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
            player = GameManger.getRoll(userName);
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
            sendMessage("Start game");
            startGame();
        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    public void startGame() {
        notifyServer();
        goToSleep();
        while (true) {
            sendMessage("Start chat:");
            startChat();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (socket.isClosed()) {
                break;
            }
            sendMessage("End chat");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (player.isSilent()) {
                player.setSilent(false);
            }
            synchronized (server.getThread()) {
                server.getThread().notify();
            }
            goToSleep();
        }
    }


    public void startChat() {
        String serverMessage;
        String clientMessage;
        try (PrintWriter fileWriter = new PrintWriter("d:\\MafiaGame.txt")) {
            while (true) {
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                if (clientMessage.equals("End")) {
                    sendMessage("End");
                    break;
                } else if (clientMessage.equals("exit")) {
                    sendMessage("exit");
                    player.setAlive(false);
                    closeAll();
                    server.removePlayerHandler(player, this);
                    serverMessage = userName + " came out.";
                    server.broadcast(serverMessage, this);
                    break;
                } else {
                    server.broadcast(serverMessage, this);
                    fileWriter.write(serverMessage);
                }
            }
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

    public void notifyServer() {
        synchronized (server.getThread()) {
            server.getThread().notify();
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


    public void exitGame() {
        try {
            player.setAlive(false);
            if (!reader.readLine().equals("Show game")) {
                server.removePlayerHandler(player, this);
                closeAll();
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void sendValue(int value) {
        writer.println(value);
    }

    public void closeAll() {
        try {
            socket.close();
            reader.close();
            objectOutputStream.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
