package com.company;

import java.net.*;
import java.io.*;

import java.util.InputMismatchException;
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


            while (true) {
                clientPlayer = (Player) objectInputStream.readObject();
                if (clientPlayer != null)
                    break;
                System.out.println("Username is duplicate Enter your username:");
                userName = scanner.next();
                writer.println(userName);
            }
            System.out.println("If you ready say : ready");

            while (true) {
                if (scanner.next().equals("ready")) {
                    writer.println("ready");
                    break;
                }
            }
            System.out.println("Waiting to anther join...");
            String msg = reader.readLine();
            if (msg.equals("Start game")) {
                System.out.println(msg);
                startGame();
            }

        } catch (IOException | ClassNotFoundException e) {
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
        try {
            System.out.println(reader.readLine());
            shareData = (ShareData) objectInputStream.readObject();
            System.out.println("users:");
            shareData.printUserNames();
            System.out.println(reader.readLine());
            if (clientPlayer instanceof MafiaPlayer) {
                for (int i = 0; i < (shareData.getPlayers().size() / 3) - 1; i++) {
                    System.out.println(reader.readLine());
                }
            }
            if (clientPlayer instanceof Mayor) {
                System.out.println(reader.readLine());
            }
            System.out.println(reader.readLine());

            while (true) {
                String msg = reader.readLine();
                if (msg.equals("Start chat:")) {
                    System.out.println(msg);
                    startChat();
                }
                try {
                    synchronized (thread) {
                        thread.wait();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (socket.isClosed()) {
                    break;
                }
                System.out.println(reader.readLine());
                voting();
                if (socket.isClosed()) {
                    break;
                }

            }


        } catch (IOException | ClassNotFoundException ioException) {
            ioException.printStackTrace();
        }

    }


    public void voting() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println(reader.readLine());
            clientPlayer = (Player) objectInputStream.readObject();
            shareData = (ShareData) objectInputStream.readObject();
            System.out.println("Get Vote");
            shareData.printOthersUserNames(userName);
            int vote = -1;
            int time1 = (int) System.currentTimeMillis();
            while (true) {
                if (vote == -1 && System.in.available() > 0 && clientPlayer.isAlive()) {
                    try {
                        vote = scanner.nextInt();
                    }
                    catch (InputMismatchException e){
                        System.out.println("Invalid");
                        continue;
                    }

                    if (vote < 1 || vote>= shareData.getNumberOfAlivePlayer()){
                        System.out.println("Enter the correct number");
                        vote = -1;
                    }
                }
                int time2 = (int) System.currentTimeMillis();
                int time = ((time2 - time1) / 1000);
                if (time > 20) {
                    break;
                }
            }
            if (vote != -1){
                writer.println(shareData.getOthersUserNames(userName).get(vote - 1));
            }
            else {
                writer.println("null");
            }
            System.out.println(reader.readLine());

            if (clientPlayer instanceof Mayor && clientPlayer.isAlive() && ((Mayor) clientPlayer).getStateAbility()){
                System.out.println(reader.readLine());
                System.out.println("1.Yes\n2.No");
                int decision;
                while (true){
                    try {
                        decision = scanner.nextInt();
                        if (decision!=1 && decision!=2){
                            System.out.println("Invalid");
                        }
                        break;
                    }
                    catch (InputMismatchException e){
                        System.out.println("Invalid");
                    }

                }
                if (decision == 1){
                    writer.println("Yes");
                }
                else if (decision == 2){
                    writer.println("No");
                }
            }
            clientPlayer = (Player) objectInputStream.readObject();
            shareData = (ShareData) objectInputStream.readObject();
            System.out.println(reader.readLine());
//            shareData.printOthersUserNames(" ");
            if (!clientPlayer.isAlive()) {
                System.out.println("You die");
                exitGame();
            }




//            if (msg.equals("Vote tamam")) {
//                System.out.println(msg);
//                clientPlayer = (Player) objectInputStream.readObject();
//            }

//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }




        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    public void startChat() {
        if (clientPlayer.isAlive() && !clientPlayer.isSilent()) {
            new ClientWrite(socket, this).start();
        }
        new ClientRead(socket, this).start();

    }

    public Thread getThread() {
        return thread;
    }

    public int exitGame() {
        Scanner scanner = new Scanner(System.in);
//        System.out.println("Are you sure to exit Game?\n1.Yes\n2.No");
//        if (scanner.nextInt() == 1){
//            writer.println("Yes exit");
        System.out.println("Do you want to see the rest of the game?\n1.Yes\n2.No");
        int decision = scanner.nextInt();
        if (decision == 1) {
            writer.println("Show game");
            clientPlayer.setAlive(false);
            return 1;
        } else if (decision == 2) {
            writer.println("Dont show");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Good bye");
            return 0;
        }
        return 0;
//            else {
//                writer.println("No exit");
//            }
//        }
    }

    public Player getClientPlayer() {
        return clientPlayer;
    }
}