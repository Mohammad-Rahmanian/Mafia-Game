package com.company;

import java.io.*;
import java.net.Socket;


/**
 * This thread handles connection for each connected client.
 *
 * @author Mohammad Rahmanian.
 * @version 1.0
 */
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

    /**
     * Instantiates a new Handler.
     *
     * @param socket the socket.
     * @param server the server.
     */
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

    /**
     * The thread of the handler.
     */

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
                if (readMessage().equals("ready"))
                    break;
            }
            shareData.addPlayer(player);
            server.addPlayerHandler(player, this);
            if (shareData.getPlayers().size() == GameManger.getNumberOfPlayers()) {
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
            ex.printStackTrace();
            System.err.println("Error in Handler.");
        }
    }

    /**
     * Start game.
     */
    public void startGame() {
        notifyServer();
        waitHandler();
        while (true) {
            sendMessage("Start chat:");
            startChat();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (socket.isClosed()) {
                closeAll();
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
            waitHandler();
        }
    }

    /**
     * Start chat.
     */
    public void startChat() {
        String serverMessage;
        String clientMessage;
        try (FileOutputStream fileOutputStream = new FileOutputStream("d:\\MafiaGame.txt", true);
             PrintWriter fileWriter = new PrintWriter(fileOutputStream, true)) {
            while (true) {
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                if (clientMessage.equals("End")) {
                    sendMessage("End");
                    break;
                } else if (clientMessage.equals("exit")) {
                    sendMessage("exit");
                    player.kill();
                    closeAll();
                    server.removePlayerHandler(player, this);
                    serverMessage = userName + " came out.";
                    server.broadcast(serverMessage, this);
                    break;
                } else {
                    server.broadcast(serverMessage, this);
                    fileWriter.println(serverMessage);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets player.
     *
     * @return the player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Read message string.
     *
     * @return the string to be read.
     */
    public String readMessage() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets thread.
     *
     * @return the thread.
     */
    public Thread getThread() {
        return thread;
    }

    /**
     * Gets user name.
     *
     * @return the user name.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Wait thread handler.
     */
    public void waitHandler() {
        try {
            synchronized (thread) {
                thread.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Notify server thread.
     */
    public void notifyServer() {
        synchronized (server.getThread()) {
            server.getThread().notify();
        }
    }

    /**
     * Sends a message to the client.
     *
     * @param message the message to be send.
     */
    public void sendMessage(String message) {
        writer.println(message);
    }

    /**
     * Send object to the client.
     *
     * @param object the object to be send.
     */
    public void sendObject(Object object) {
        try {
            objectOutputStream.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Exit game.
     */
    public void exitGame() {
        player.kill();
        if (!readMessage().equals("Show game")) {
            server.removePlayerHandler(player, this);
            closeAll();
        }
    }

    /**
     * Send value.
     *
     * @param value the value to be send.
     */
    public void sendValue(int value) {
        writer.println(value);
    }

    /**
     * Close all streams.
     */
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
