package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The server of the mafia game.
 *
 * @author Mohammad Rahmanian.
 * @version 1.0
 */
public class Server {
    private Thread thread;
    private ShareData shareData;
    private HashMap<Player, Handler> playerHandler;

    /**
     * Instantiates a new Server.
     */
    public Server() {
        thread = Thread.currentThread();
        shareData = new ShareData();
        playerHandler = new HashMap<>();
    }

    /**
     * Execute sever.
     */
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of players between 8 and 10:");
        int numberOfPlayers;
        while (true) {
            try {
                numberOfPlayers = scanner.nextInt();
                if (numberOfPlayers < 8 || numberOfPlayers > 10) {
                    System.out.println("Enter the number of players between 8 and 10:");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.err.println("Invalid input.");
                System.out.println("Please try again:");
                scanner.nextLine();
            }
        }
        GameManger.setNumberOfPlayers(numberOfPlayers);
        GameManger.setRolls();
        ExecutorService executorService = Executors.newCachedThreadPool();
        try (ServerSocket serverSocket = new ServerSocket(6000)) {
            System.out.println("Server started.\nWaiting for a client ... ");
            int counter = 0;
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client accepted!");
                Handler newUser = new Handler(socket, this);
                executorService.execute(newUser);
                counter++;
                if (counter == numberOfPlayers) {
                    break;
                }
            }
            executorService.shutdown();
        } catch (IOException e) {
            System.err.println("Error in the server.");
            e.printStackTrace();
        }
    }

    /**
     * Gets thread.
     *
     * @return the thread.
     */
    public synchronized Thread getThread() {
        return thread;
    }


    /**
     * Delivers a message from one user to others.
     *
     * @param message     the message.
     * @param excludeUser the exclude user.
     */
    public void broadcast(String message, Handler excludeUser) {
        ArrayList<Handler> handlers = new ArrayList<>(playerHandler.values());
        for (Handler h : handlers) {
            if (h != excludeUser) {
                h.sendMessage(message);
            }
        }
    }

    /**
     * Gets share data.
     *
     * @return the share data.
     */
    public ShareData getShareData() {
        return shareData;
    }


    /**
     * Notify handlers.
     */
    public void notifyHandlers() {
        ArrayList<Handler> handlers = new ArrayList<>(playerHandler.values());
        for (Handler handler : handlers) {
            synchronized (handler.getThread()) {
                handler.getThread().notify();
            }
        }
    }

    /**
     * Add player handler.
     *
     * @param player  the player.
     * @param handler the handler.
     */
    public void addPlayerHandler(Player player, Handler handler) {
        playerHandler.put(player, handler);
    }

    /**
     * Gets player handler.
     *
     * @return the player handler.
     */
    public HashMap<Player, Handler> getPlayerHandler() {
        return playerHandler;
    }

    /**
     * Gets handler.
     *
     * @param player the player.
     * @return the handler of the player.
     */
    public Handler getHandler(Player player) {
        return playerHandler.get(player);
    }

    /**
     * Remove player handler.
     *
     * @param player  the player to be removed.
     * @param handler the handler to be removed.
     */
    public void removePlayerHandler(Player player, Handler handler) {
        playerHandler.remove(player, handler);
    }


    /**
     * Main thread of the server.
     *
     * @param args the args.
     */
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


}






