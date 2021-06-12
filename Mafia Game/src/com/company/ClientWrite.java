package com.company;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Scanner;

/**
 * This thread is responsible for reading user's input and send it
 * to the server.
 * It runs in an infinite loop until the user types 'bye' to quit.
 *
 * @author www.codejava.net
 */
public class ClientWrite extends Thread {
    private PrintWriter writer;
    private Socket socket;
    private Client client;

    public ClientWrite(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {

        Scanner scanner = new Scanner(System.in);
        String userName = client.getUserName();
        String text = "";
        int time;
        Date start = new Date();

        do {

            time = (int) ((new Date().getTime() - start.getTime()) / 1000);
            if (time > 10) {
                writer.println("End");

                break;
            }


            try {
                if (System.in.available() > 0 && client.getClientPlayer().isAlive()) {
                    text = scanner.next();
                    if (text.equals("exit")) {
                        System.out.println("Do you want to see the rest of the game?\n1.Yes\n2.No");
                        int decision = scanner.nextInt();
                        if (decision == 1) {
                            writer.println("exit1");
                            client.getClientPlayer().setAlive(false);
                        } else if (decision == 2) {
                            writer.println("exit0");
                            try {
                                socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println("Good bye");
                            break;
                        }
                    } else {
                        writer.println(text);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }


        } while (true);


    }
}

