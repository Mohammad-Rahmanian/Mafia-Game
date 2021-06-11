package com.company;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ShareData implements Serializable {

    private ArrayList<String> rolls;
    private ArrayList<String> userNames;
    ArrayList<Player> players;

    public ShareData() {
        rolls = new ArrayList<>();
        userNames = new ArrayList<>();
        players = new ArrayList<>();

    }

    public ShareData(HashMap<Player, Handler> playerHandler, ArrayList<String> rolls, ArrayList<String> userNames) {
        this.rolls = rolls;
        this.userNames = userNames;
    }

    public synchronized void printUserNames() {
        int counter = 1;
        for (String userName : userNames) {
            System.out.println(counter + ")" + userName);
            counter++;
        }
    }

    public synchronized void printOthersUserNames(String userName) {
        int counter = 1;
        for (String u : userNames) {
            if (!u.equals(userName)) {
                System.out.println(counter + ")" + userName);
                counter++;
            }
        }
    }

    public synchronized String getUserNamesString(Handler handler) {
        StringBuilder builder = new StringBuilder();
        int counter = 1;
        for (String userName : userNames) {
            if (!handler.getUserName().equals(userName)) {
                builder.append(counter).append(")").append(userName).append("\n");
                counter++;
            }

        }
        return builder.toString();
    }


    public boolean checkUserName(String userName) {
        for (String u : userNames) {
            if (u.equals(userName)) {
                return true;
            }
        }
        return false;
    }

    public Player findPlayerByUserName(String userName) {
        for (Player player : players) {
            if (player.getUserName().equals(userName)) {
                return player;
            }
        }
        return null;
    }


    public void addUserName(String userName) {
        userNames.add(userName);
    }


    public ArrayList<String> getUserNames() {
        return userNames;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Player findPlayerByRoll(String roll) {
        for (Player player : players) {
            if (player.getRoll().equals(roll)) {
                return player;
            }
        }
        return null;
    }

    public int getNumberOfAlivePlayer() {
        int numberOfAlivePlayer = 0;
        for (Player player : players) {
            if (player.getState()) {
                numberOfAlivePlayer++;
            }
        }
        return numberOfAlivePlayer;
    }
    public Player getPlayer(int index){
        return players.get(index);
    }
    public String getUserName(int index){
        return userNames.get(index);
    }
}
