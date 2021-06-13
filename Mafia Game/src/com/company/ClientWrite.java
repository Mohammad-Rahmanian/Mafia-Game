package com.company;

import java.io.*;
import java.net.*;
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

    public ClientWrite(Socket socket) {
        this.socket = socket;

        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        String text;
        int time1 = (int) System.currentTimeMillis();
        while (true) {
            int time2 = (int) System.currentTimeMillis();
            int time = ((time2 - time1) / 1000);
            if (time > 60) {
                writer.println("End");
                break;
            }
            try {
                if (System.in.available() > 0) {
                    writer.println("is typing ...");
                    text = scanner.next();
                    if (text.equals("exit")) {
                        writer.println("exit");
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Good bye");
                        break;
                    } else {
                        writer.println(text);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

    }
}

