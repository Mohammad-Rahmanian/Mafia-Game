package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class ShareData {
    private  HashMap<Player, Handler> playerHandler;
    private  ArrayList<String> rolls;
    private  ArrayList<String> userNames;

    public ShareData() {
        playerHandler = new HashMap<>();
        rolls = new ArrayList<>();
        userNames = new ArrayList<>();

    }
    public ShareData(HashMap<Player, Handler> playerHandler,ArrayList<String> rolls,ArrayList<String> userNames){
        this.playerHandler = playerHandler;
        this.rolls = rolls;
        this.userNames = userNames;
    }

    public  void printUserNames() {
        int counter = 1;
        for (String userName : userNames) {
            System.out.println(counter + ")" + userName);
            counter++;
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
        ArrayList<Player> players = new ArrayList<>(playerHandler.keySet());
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

    public  void addPlayerHandler(Player player, Handler handler) {
        playerHandler.put(player, handler);
    }

    public  HashMap<Player, Handler> getPlayerHandler() {
        return playerHandler;
    }


    public ArrayList<Player> getPlayers() {
        return new ArrayList<>(playerHandler.keySet());
    }
    public ArrayList<String> getUserNames() {
        return userNames;
    }



}
