package com.company;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * This is the client program.
 *
 * @author Mohammad Rahmanian.
 * @version 1.0
 */
public class Client {

    private String userName;
    private Player clientPlayer;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private ObjectInputStream objectInputStream;
    private Thread thread;
    private ArrayList<String> aliveUsers;
    private boolean isWatchGame;

    /**
     * Instantiates a new Client.
     */
    public Client() {
        aliveUsers = new ArrayList<>();
        isWatchGame = false;
    }

    /**
     * Execute client program.
     */
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("Enter the port and server:");
            while (true) {
                try {
                    System.out.println("Server:");
                    String serverHost = scanner.next();
                    System.out.println("Port:");
                    int port = scanner.nextInt();
                    socket = new Socket(serverHost, port);
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Cannot connect to server.");
                    System.out.println("Please try again:");
                } catch (InputMismatchException e) {
                    System.err.println("Invalid input.");
                    System.out.println("Please try again:");
                    scanner.nextLine();
                }
            }
            socket = new Socket("localhost", 6000);
            System.out.println("Connected to the  server.");
            System.out.println("Welcome to the Mafia game.");
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            thread = Thread.currentThread();
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("Enter your username:");
            userName = scanner.next();
            sendMessage(userName);
            while (true) {
                clientPlayer = (Player) objectInputStream.readObject();
                if (clientPlayer != null)
                    break;
                System.out.println("This username has already been used.");
                System.out.println("Please enter another username.");
                userName = scanner.next();
                writer.println(userName);
            }
            System.out.println("Are you ready to start the game?\n1.yes");
            while (true) {
                try {
                    int readyDecision = scanner.nextInt();
                    if (readyDecision == 1) {
                        writer.println("ready");
                        break;
                    } else {
                        System.out.println("The game will not start until you enter the 1");
                    }
                } catch (InputMismatchException e) {
                    System.err.println("Invalid input.");
                    System.out.println("Please try again:");
                    scanner.nextLine();
                }
            }
            System.out.println("Waiting to anther join...");
            Thread.sleep(1000);
            System.out.println(readMessage());
            startGame();
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start game.
     */
    public void startGame() {
        introductionNight();
        try {
            while (true) {
                String msg = readMessage();
                if (msg.equals("End match")) {
                    System.out.println(readMessage());
                    System.out.println("Good bye.");
                    socket.close();
                    closeALl();
                    break;
                }
                System.out.println(msg);
                if (!clientPlayer.isAlive() && !isWatchGame) {
                    System.out.println("You dead.");
                    exitGame();
                }
                if (socket.isClosed()) {
                    closeALl();
                    break;
                }
                getTheLatestAliveUserList();
                System.out.println("Alive Users:");
                printUserNames();
                msg = readMessage();
                while (!msg.equals("Start chat:")) {
                    Thread.sleep(500);
                    System.out.println(msg);
                    msg = readMessage();
                }
                Thread.sleep(1000);
                System.out.println(msg);
                startChat();
                synchronized (thread) {
                    thread.wait();
                }
                if (socket.isClosed()) {
                    closeALl();
                    break;
                }
                System.out.println(reader.readLine());
                Thread.sleep(1000);
                System.out.println(reader.readLine());
                voting();
                if (socket.isClosed()) {
                    closeALl();
                    break;
                }
                nightGame();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Introduction night.
     */
    public void introductionNight() {
        try {
            System.out.println(readMessage());
            Thread.sleep(2000);
            getTheLatestAliveUserList();
            System.out.println("ALive users:");
            printUserNames();
            Thread.sleep(1000);
            System.out.println(readMessage());
            Thread.sleep(1000);
            if (clientPlayer instanceof MafiaPlayer) {
                for (int i = 0; i < (aliveUsers.size() / 3) - 1; i++) {
                    System.out.println(readMessage());
                }
            }
            if (clientPlayer instanceof Mayor) {
                System.out.println(readMessage());
            }
            Thread.sleep(2000);
            System.out.println(readMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start chat.
     */
    public void startChat() {
        new ClientWrite(this).start();
        new ClientRead(this).start();
    }

    /**
     * Voting.
     */
    public void voting() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println(readMessage());
            Thread.sleep(1000);
            getTheLatestAliveUserList();
            System.out.println("Please vote for someone:");
            printOthersUserNames();
            int vote = -1;
            int time1 = (int) System.currentTimeMillis();
            while (true) {
                if (System.in.available() > 0 && clientPlayer.isAlive()) {
                    try {
                        vote = scanner.nextInt();
                        if (vote < 1 || vote >= aliveUsers.size()) {
                            System.out.println("Enter the number between 1 and " + getOthersUserNames().size());
                            vote = -1;
                        } else {
                            System.out.println("You can change your vote.");
                        }
                    } catch (InputMismatchException e) {
                        System.err.println("Invalid input.");
                        System.out.println("Please try again.");
                        scanner.nextLine();
                    }
                }
                int time2 = (int) System.currentTimeMillis();
                int time = ((time2 - time1) / 1000);
                if (time > 20) {
                    break;
                }
            }
            System.out.println("End voting");
            Thread.sleep(1000);
            if (vote != -1) {
                sendMessage(getOthersUserNames().get(vote - 1));
                System.out.println("You vote " + getOthersUserNames().get(vote - 1));
            } else {
                if (clientPlayer.isAlive()) {
                    sendMessage("null");
                    System.out.println("You did not vote for anyone");
                }
            }
            if (clientPlayer.isAlive()) {
                for (int i = 0; i < aliveUsers.size() - 1; i++) {
                    System.out.println(readMessage());
                }
            } else
                for (int i = 0; i < aliveUsers.size(); i++) {
                    System.out.println(readMessage());
                }

            if ((clientPlayer instanceof Mayor) && clientPlayer.isAlive()) {
                if (((Mayor) clientPlayer).getStateAbility()) {
                    clientPlayer.act(this);
                }
            }
            Thread.sleep(2000);
            clientPlayer = (Player) objectInputStream.readObject();
            getTheLatestAliveUserList();
            System.out.println(readMessage());
            if (!clientPlayer.isAlive() && !isWatchGame) {
                exitGame();
            }
            Thread.sleep(1000);
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Night game.
     */
    public void nightGame() {
        System.out.println(readMessage());
        if (clientPlayer.isAlive()) {
            String message = readMessage();
            label:
            while (true) {
                switch (message) {
                    case "Act":
                        if (!(clientPlayer instanceof Mayor)) {
                            clientPlayer.act(this);
                        }
                        message = readMessage();
                        break;
                    case "Get vote":
                        ((MafiaPlayer) clientPlayer).getVote(this);
                        message = readMessage();
                        break;
                    case "Kill":
                        ((MafiaPlayer) clientPlayer).killCitizen(this);
                        message = readMessage();
                        break;
                    case "End night":
                        System.out.println(message);
                        break label;
                    default:
                        System.out.println(message);
                        message = readMessage();
                        break;
                }
            }
        } else {
            String message = readMessage();
            while (true) {
                System.out.println(message);
                message = readMessage();
                if (message.equals("End night")) {
                    break;
                }
            }

        }
        try {
            clientPlayer = (Player) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Print user names.
     */
    public void printUserNames() {
        int counter = 1;
        for (String userName : aliveUsers) {
            System.out.println(counter + ")" + userName);
            counter++;
        }
    }

    /**
     * Print others user names.
     */
    public void printOthersUserNames() {
        int counter = 1;
        for (String userName : aliveUsers) {
            if (!userName.equals(this.userName)) {
                System.out.println(counter + ")" + userName);
                counter++;
            }
        }
    }

    /**
     * Gets others user names.
     *
     * @return the others user names
     */
    public ArrayList<String> getOthersUserNames() {
        ArrayList<String> userNames = new ArrayList<>();
        for (String userName : aliveUsers) {
            if (!userName.equals(this.userName)) {
                userNames.add(userName);
            }
        }
        return userNames;
    }


    /**
     * Gets thread.
     *
     * @return the thread
     */
    public Thread getThread() {
        return thread;
    }

    /**
     * Exit game.
     */
    public void exitGame() {
        System.out.println("Do you want to see the rest of the game?\n1.Yes\n2.No");
        int decision = yesOrNoQuestion();
        if (decision == 1) {
            writer.println("Show game");
            clientPlayer.kill();
            isWatchGame = true;
        } else if (decision == 2) {
            writer.println("Dont show");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Good bye");
        }

    }

    /**
     * Gets client player.
     *
     * @return the client player
     */
    public Player getClientPlayer() {
        return clientPlayer;
    }

    /**
     * Send message.
     *
     * @param message the message to be send.
     */
    public void sendMessage(String message) {
        writer.println(message);
    }

    /**
     * Read message string.
     *
     * @return the string to be read.
     */
    public String readMessage() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets user name.
     *
     * @return the user name of the client.
     */
    String getUserName() {
        return this.userName;
    }

    /**
     * Close streams.
     */
    public void closeALl() {
        try {
            writer.close();
            reader.close();
            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets socket.
     *
     * @return the socket of the client.
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Gets the latest alive user list.
     */
    public void getTheLatestAliveUserList() {
        aliveUsers = new ArrayList<>();
        int numberOfPlayers = Integer.parseInt(readMessage());
        for (int i = 0; i < numberOfPlayers; i++) {
            String userName = readMessage();
            aliveUsers.add(userName);
        }
    }

    /**
     * This method is for selecting the user.
     *
     * @param users the users to be selected.
     * @return the Selected user.
     */
    public String selectUser(ArrayList<String> users) {
        Scanner scanner = new Scanner(System.in);
        int counter = 1;
        for (String user : users) {
            System.out.println(counter + ")" + user);
            counter++;
        }
        int decision;
        while (true) {
            try {
                decision = scanner.nextInt();
                if (decision < 1 || decision > users.size()) {
                    System.out.println("Enter a number between 1 and " + users.size());
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.err.println("Invalid input");
                scanner.nextLine();
            }
        }
        return users.get(decision - 1);
    }

    /**
     * This method is for answering yes or no questions.
     *
     * @return the answer of the client.
     */
    public int yesOrNoQuestion() {
        Scanner scanner = new Scanner(System.in);
        int decision;
        while (true) {
            try {
                decision = scanner.nextInt();
                if (decision != 1 && decision != 2) {
                    System.out.println("Enter 1 or 2");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.err.println("Invalid input");
                scanner.nextLine();
            }
        }
        return decision;
    }


    /**
     * The main method for client.
     *
     * @param args the input arguments.
     */
    public static void main(String[] args) {
        Client client = new Client();
        client.execute();
    }


}






