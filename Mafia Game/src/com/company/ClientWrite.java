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
//            text = console.readLine("[" + userName + "]: ");


            try {
                if (System.in.available() > 0) {
                    text = scanner.next();
                    writer.println(text);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            time = (int) ((new Date().getTime() - start.getTime()) / 1000);
            if (time > 10) {
                writer.println("End");
                synchronized (client.getThread()) {
                    client.getThread().notify();
                }
                break;
            }
            if (text.equals("exit")) {
                try {
                    socket.close();
                } catch (IOException ex) {

                    System.out.println("Error writing to server: " + ex.getMessage());
                }
            }

        } while (!text.equals("bye"));


    }
}

