package com.company;

import java.io.*;
import java.util.Scanner;

/**
 * This thread is  for reading user input and send it to the server.
 *
 * @author www.codejava.net
 */
public class ClientWrite extends Thread {
    private Client client;


    /**
     * Instantiates a new Client write.
     *
     * @param client the client.
     */
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
            if (time > 60) {
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

