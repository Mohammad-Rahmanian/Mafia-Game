package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    HashMap<Player, Handler> playerHandler;
    ArrayList<String> rolls;
    ArrayList<String> userNames;


    public Server() {
        playerHandler = new HashMap<>();
        rolls = new ArrayList<>();
        userNames = new ArrayList<>();
    }


    public void execute() {
        ArrayList<Thread> threads = new ArrayList<>();
        String[] rollArray = new String[]{"Godfather", "Dr.Lecter", "Simple Mafia", "City Doctor"
                , "Simple Citizen", "Detective", "Diehard", "Psychologist", "Professional", "Mayor"};
        rolls = new ArrayList<>(Arrays.asList(rollArray));
        ExecutorService executorService = Executors.newCachedThreadPool();
        try (ServerSocket serverSocket = new ServerSocket(6969)) {
            System.out.println("Chat Server is listening on port");
            int counter = 0;
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");
                Handler newUser = new Handler(socket, this);
//                executorService.execute(newUser);
                Thread thread = new Thread(newUser);
                threads.add(thread);
                thread.start();
                counter++;
                if (counter == 2) {
                    break;
                }
            }


//            try {
//                synchronized (Thread.currentThread()){
//                    Thread.currentThread().wait();
//                }
//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println("psasa");


//            try {
//                Thread.currentThread().wait();
//                System.out.println("pashmam");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            Thread.currentThread().;
//            startGame();
//            executorService.shutdown();

        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }


    }

    public synchronized static void main(String[] args) {
        Server server = new Server();
        server.execute();
//        if (server.getPlayerHandler().size()!= 2){
//            try {
//                synchronized (server.playerHandler){
//                    server.getPlayerHandler().wait();
//                }
//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        server.startGame();


    }

    public HashMap<Player, Handler> getPlayerHandler() {
        return playerHandler;
    }

    public void startGame(){
        ArrayList<Player> players = new ArrayList<>(playerHandler.keySet());
        ArrayList<Handler> handlers = new ArrayList<>(playerHandler.values());
//        ArrayBlockingQueue<Handler> handlersQueue = new ArrayBlockingQueue<Handler>(playerHandler.size());
//        handlersQueue.addAll(handlers);

        broadcast("Start game",null);
        broadcast("Users:",null);
//        for (Handler handler : handlers){
//            handler.sendObject(getUserNamesString());
//        }
        broadcast("Introduction night",null);
        for (Handler handler : handlers){
            handler.sendMessage("your roll:" + handler.getPlayer().getRoll());
//            introductionNight(handler);
        }
        broadcast("Start chat:",null);
//        for (Handler handler : handlersQueue){
////            handler.sendMessage("Start chat:");
//            handler.startChat();
//        }
        for (Handler handler : handlers){
            handler.startChat();
        }



    }

    public ArrayList<Player> getPlayers() {
        return new ArrayList<>(playerHandler.keySet());
    }

    public void addPlayerHandler(Player player, Handler handler) {
        playerHandler.put(player, handler);
    }

    public synchronized Thread getThread() {
        return Thread.currentThread();
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

    public ArrayList<String> getUserNames() {
        return userNames;
    }

    public synchronized String getUserNamesString() {
        StringBuilder builder = new StringBuilder();
        int counter = 1;
        for (String userName : userNames) {
            builder.append(counter).append(")").append(userName).append("\n");
            counter++;
        }
        return builder.toString();
    }

    public boolean checkUser(String userName) {
        ArrayList<Player> players = new ArrayList<>(playerHandler.keySet());
        for (Player player : players) {
            if (player.getUserName().equals(userName)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkUserName(String userName) {
        for (String u : userNames) {
            if (u.equals(userName)) {
                return true;
            }
        }
        return false;
    }

    public void addUserName(String userName) {
        if (userName != null)
            userNames.add(userName);
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
    void removePlayer(Player player) {
        playerHandler.remove(player);
    }


    //
//    Set<String> getUserNames() {
//        return this.userNames;
//    }
    public Player setRoll(String userName) {

        int randomValue = new Random().nextInt(rolls.size());
        Player player = new PlayerFactory().getPlayer(rolls.get(randomValue), userName);
        rolls.remove(randomValue);
        return player;

    }

    public boolean checkEndOfGame() {
        ArrayList<Player> players = new ArrayList<>(playerHandler.keySet());
        int mafiaNumber = 0;
        int citizenNumber = 0;
        for (Player player : players) {
            if (player instanceof MafiaPlayer) {
                mafiaNumber++;
            } else if (player instanceof CitizenPlayer) {
                citizenNumber++;
            }
        }
        if (mafiaNumber >= citizenNumber || mafiaNumber == 0) {
            return true;
        }
        return false;
    }

    public synchronized void introductionNight(Handler handler) {
        ArrayList<Player> mafiaPlayers = new ArrayList<>();
        ArrayList<Player> players = new ArrayList<>(playerHandler.keySet());
        for (Player player : players) {
            if (player instanceof MafiaPlayer) {
                mafiaPlayers.add(player);
            }
        }
        if (handler.getPlayer() instanceof MafiaPlayer) {
            for (int i = 0; i < mafiaPlayers.size(); i++) {
                if (mafiaPlayers.get(i) != handler.getPlayer()) {
                    handler.sendMessage(mafiaPlayers.get(i).getUserName() + ":" + mafiaPlayers.get(i).getRoll());
//                    System.out.println(mafiaPlayers.get(i).getUserName() + ":" + mafiaPlayers.get(i).getRoll());
                }
            }
        }

    }

    public Player findPlayer(String roll) {
        ArrayList<Player> players = new ArrayList<>(playerHandler.keySet());
        for (Player player : players) {
            if (player.getRoll().equals(roll)) {
                return player;
            }
        }
        return null;
    }

//    public void startGame() {
//        ArrayList<Handler> handlers = new ArrayList<>(playerHandler.values());
//        for (Handler h : handlers){
//            h.start();
//        }


//        try {
//            Thread.currentThread().join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        ArrayList<Handler> handlers = new ArrayList<>(playerHandler.values());
//        broadcast("Users:",null);
//        broadcast(getUserNamesString(),null);
//        broadcast("Introduction night",null);
//        for (Handler handler : handlers){
//            handler.sendMessage("your roll :" + handler.getPlayer().getRoll());
//            introductionNight(handler);
//        }
//
//        broadcast("Start chat",null);
//        for (Handler handler : handlers) {
//            handler.startChat();
//        }
}












