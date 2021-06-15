package com.company;

import java.io.*;
import java.util.Scanner;

/**
 * This thread is responsible for reading user's input and send it
 * to the server.
 * It runs in an infinite loop until the user types 'bye' to quit.
 *
 * @author www.codejava.net
 */
public class ClientWrite extends Thread {
    private Client client;


    public ClientWrite(Client client) {
        this.client = client;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        String text;
        int time1 = (int) System.currentTimeMillis();
        while (true) {
            int time2 = (int) System.currentTimeMillis();
            int time = ((time2 - time1) / 1000);
            if (time > 10) {
                client.sendMessage("End");
                break;
            }
            try {
                if (System.in.available() > 0 && client.getClientPlayer().isAlive() && !client.getClientPlayer().isSilent()) {
                    text = scanner.nextLine();
                    if (text.equals("exit")) {
                        client.sendMessage("exit");
                        try {
                            client.getSocket().close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Good bye");
                        break;
                    } else {
                        client.sendMessage(text);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

    }
}

