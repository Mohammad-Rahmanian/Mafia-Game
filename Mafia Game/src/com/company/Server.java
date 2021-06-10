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
        System.out.println("Enter the number of players");
        numberOfPlayers = scanner.nextInt();
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
        GameManger gameManger = new GameManger(server, server.getShareData(), server.getNumberOfPlayers());
        try {
            synchronized (server.getThread()) {
                server.getThread().wait();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        gameManger.introductionNight();
        server.notifyHandlers();


    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void waitHandlers() {
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


//        /**
//     * When a client is disconneted, removes the associated username and UserThread
//     */
//    void removeUser(String userName, UserThread aUser) {
//        boolean removed = userNames.remove(userName);
//        if (removed) {
//            userThreads.remove(aUser);
//            System.out.println("The user " + userName + " quitted");
//        }
//    }

    /**
     * //     * When a client is disconneted, removes the associated username and UserThread
     * //
     */


        public void addPlayerHandler(Player player, Handler handler) {
        playerHandler.put(player, handler);
    }

    public HashMap<Player, Handler> getPlayerHandler() {
        return playerHandler;
    }


    public ArrayList<Player> getPlayers() {
        return new ArrayList<>(playerHandler.keySet());
    }


}












