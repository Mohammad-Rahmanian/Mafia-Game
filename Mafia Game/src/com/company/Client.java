package com.company;

import java.net.*;
import java.io.*;

import java.util.Scanner;

/**
 * This is the chat client program.
 * Type 'bye' to terminte the program.
 *
 * @author www.codejava.net
 */
public class Client {

    private String userName;
    private Player clientPlayer;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private ObjectInputStream objectInputStream;
    private DataInputStream dataInputStream;
    private Thread thread;
    private ShareData shareData;


    public void execute() {
        Scanner scanner = new Scanner(System.in);
        try {
            socket = new Socket("localhost", 6969);

            System.out.println("Connected to the  server");
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Enter your username:");
            userName = scanner.next();
            writer.println(userName);
            thread = Thread.currentThread();

            dataInputStream = new DataInputStream(socket.getInputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            String msg = reader.readLine();

            while (msg.equals("Error")) {
                System.out.println(msg);
                System.out.println("Username is duplicate Enter your username:");
                userName = scanner.next();
                writer.println(userName);
                msg = reader.readLine();
            }

            System.out.println(msg);
            while (true) {
                if (scanner.next().equals("ready")) {
                    writer.println("ready");
                    break;
                }
            }
            System.out.println("Waiting to anther join...");
            msg = reader.readLine();
            if (msg.equals("Start game")) {
                System.out.println(msg);
                startGame();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    String getUserName() {
        return this.userName;
    }


    public static void main(String[] args) {
        Client client = new Client();
        client.execute();
    }

    public void startGame() {


        Scanner scanner = new Scanner(System.in);
//        try {
//            System.out.println(reader.readLine());
//            clientPlayer = (Player) objectInputStream.readObject();
//            System.out.println("your roll" + clientPlayer.getRoll());
//            shareData = (ShareData) objectInputStream.readObject();
//
////            System.out.println(reader.readLine());
//
//            if (clientPlayer instanceof MafiaPlayer) {
//                for (int i = 0; i < shareData.getPlayers().size() - 1; i++) {
//                    System.out.print(reader.readLine());
//                }
//            }
//            System.out.println("Users:");
//            shareData.printUserNames();
//                        System.out.println(reader.readLine());
//
////            String msg = reader.readLine();
////            if (msg.equals("Start chat:")) {
////                System.out.println(msg);
////                startChat();
////            }
////            try {
////                synchronized (thread) {
////                    thread.wait();
////                }
////
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
////            System.out.println(reader.readLine());
//////            System.out.println(reader.readLine());
//////             msg = (String) objectInputStream.readObject();
//////            System.out.println(msg);
////            msg = reader.readLine();
////            if (msg.equals("Vote bede")) {
////                System.out.println(msg);
//////                writer.println(scanner.nextInt());
////                String message = null;
////                Date start = new Date();
////                while (true) {
////                    if (message == null && System.in.available() > 0) {
////                        message = scanner.next();
////                    }
////                    int time = (int) ((new Date().getTime() - start.getTime()) / 1000);
////                    if (time > 10) {
////                        break;
////                    }
////                }
////
////                if (message == null) {
////                    writer.println("-1");
////                } else writer.println(message);
////            }
////            msg = reader.readLine();
////            if (msg.equals("Vote tamam")) {
////                System.out.println(msg);
////                clientPlayer = (Player) objectInputStream.readObject();
////            }
//////                System.out.println(reader.readLine());
//////            try {
//////                Thread.sleep(1000);
//////            } catch (InterruptedException e) {
//////                e.printStackTrace();
//////            }
////
////            if (!clientPlayer.getState()) {
////                System.out.println("You die");
////            }
////                System.out.println(dataInputStream.readUTF());
//////                ManageData.printUserNames();
//////                ManageData.getInstance().printUserNames();
//////                Server.printUserNames();
//        } catch (IOException | ClassNotFoundException ioException) {
//            ioException.printStackTrace();
//        }
//
////            writer.println(scanner.nextInt());
//
//
////            System.out.println(reader.readLine());

    }
//        catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }


    public void startChat() {
        new ClientWrite(socket, this).start();
        new ClientRead(socket, this).start();

    }

    public Thread getThread() {
        return thread;
    }
}