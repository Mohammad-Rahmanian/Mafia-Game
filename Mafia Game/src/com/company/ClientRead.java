package com.company;

/**
 * This thread for reading server's input and printing it.
 *
 * @author Mohammad Rahmanian.
 * @version 1.0
 */
public class ClientRead extends Thread {
    private Client client;

    /**
     * Instantiates a new Client read.
     *
     * @param client the client.
     */
    public ClientRead(Client client) {
        this.client = client;
    }

    /**
     * The thread of the class.
     */
    @Override
    public void run() {
        while (true) {
            String response = client.readMessage();
            if (response.equals("exit") || response.equals("End")) {
                synchronized (client.getThread()) {
                    client.getThread().notify();
                }
                break;
            }
            else {
                System.out.println(response);
            }
        }
    }
}

