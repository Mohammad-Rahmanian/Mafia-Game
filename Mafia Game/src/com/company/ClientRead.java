package com.company;
/**
 * This thread is responsible for reading server's input and printing it
 * to the console.
 * It runs in an infinite loop until the client disconnects from the server.
 *
 * @author www.codejava.net
 */
public class ClientRead extends Thread {
    private Client client;

    public ClientRead(Client client) {
        this.client = client;
    }

    public void run() {
        while (true) {
            String response = client.readMessage();
            if (response.equals("exit") || response.equals("End")) {
                synchronized (client.getThread()) {
                    client.getThread().notify();
                }
                break;
            } else if (response.equals("End voting")){
                System.out.println(response);
                break;
            }
            else {
                System.out.println(response);
            }
        }
    }

}

