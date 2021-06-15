package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private Thread thread;
    private ShareData shareData;
    private HashMap<Player, Handler> playerHandler;
    private int numberOfPlayers;


    public Server() {
        thread = Thread.currentThread();
        shareData = new ShareData();
        playerHandler = new HashMap<>();
    }

    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of players:");
        numberOfPlayers = scanner.nextInt();
        GameManger.setNumberOfPlayers(numberOfPlayers);
        GameManger.setRolls();
        ExecutorService executorService = Executors.newCachedThreadPool();
        try (ServerSocket serverSocket = new ServerSocket(6969)) {
            System.out.println("Chat Server is listening on port");
            int counter = 0;
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");
                Handler newUser = new Handler(socket, this);
                executorService.execute(newUser);
                counter++;
                if (counter == numberOfPlayers) {
                    break;
                }
            }
            executorService.shutdown();
        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    public synchronized static void main(String[] args) {
        Server server = new Server();
        server.execute();
        GameManger gameManger = new GameManger(server, server.getShareData());

        try {
            synchronized (server.getThread()) {
                server.getThread().wait();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        gameManger.startGame();
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }


    public synchronized Thread getThread() {
        return thread;
    }


    /**
     * Delivers a message from one user to others (broadcasting)
     */
    public void broadcast(String message, Handler excludeUser) {
        ArrayList<Handler> handlers = new ArrayList<>(playerHandler.values());
        for (Handler h : handlers) {
            if (h != excludeUser) {
                h.sendMessage(message);
            }
        }
    }

    public ShareData getShareData() {
        return shareData;
    }


    public void notifyHandlers() {
        ArrayList<Handler> handlers = new ArrayList<>(playerHandler.values());
        for (Handler handler : handlers) {
            synchronized (handler.getThread()) {
                handler.getThread().notify();
            }
        }
    }

    public void addPlayerHandler(Player player, Handler handler) {
        playerHandler.put(player, handler);
    }

    public HashMap<Player, Handler> getPlayerHandler() {
        return playerHandler;
    }

    public Handler getHandler(Player player) {
        return playerHandler.get(player);
    }

    public void removePlayer(Player player) {
        playerHandler.remove(player);
    }

    public void removePlayerHandler(Player player, Handler handler) {
        playerHandler.remove(player, handler);
    }
}






