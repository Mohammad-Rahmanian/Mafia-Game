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
    private ArrayList<Handler> handlers;


    public Server() {
        thread = Thread.currentThread();
        shareData = new ShareData();
    }


    public void execute() {
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
                if (counter == 2) {
                    break;
                }
            }
        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public synchronized static void main(String[] args) {
        Server server = new Server();
        server.execute();
        GameManger gameManger = new GameManger();
        try {
            synchronized (server.getThread()) {
                server.getThread().wait();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }



//        if (server.getPlayerHandler().size()!= 2){

//        server.startGame();


    }


    public void waitHandlers(){
    }





    public synchronized Thread getThread() {
        return thread;
    }


    /**
     * Delivers a message from one user to others (broadcasting)
     */
    public void broadcast(String message, Handler excludeUser) {
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




}












