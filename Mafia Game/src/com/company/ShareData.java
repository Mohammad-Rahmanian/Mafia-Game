package com.company;

import java.util.ArrayList;


public class ShareData {

    private ArrayList<String> userNames;
    private ArrayList<Player> players;


    public ShareData() {
        userNames = new ArrayList<>();
        players = new ArrayList<>();

    }


    public ArrayList<String> getMafiaPlayersUserNames() {
        ArrayList<String> mafiaPlayersUserNames = new ArrayList<>();
        for (Player player : players) {
            if (player.isAlive() && player instanceof MafiaPlayer) {
                mafiaPlayersUserNames.add(player.getUserName());
            }
        }
        return mafiaPlayersUserNames;
    }

    public ArrayList<String> getCitizenPlayersUserNames() {
        ArrayList<String> citizenPlayerUserNames = new ArrayList<>();
        for (Player player : players) {
            if (player.isAlive() && player instanceof CitizenPlayer) {
                citizenPlayerUserNames.add(player.getUserName());
            }
        }
        return citizenPlayerUserNames;
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
            if (player.isAlive()) {
                numberOfAlivePlayer++;
            }
        }
        return numberOfAlivePlayer;
    }

    public ArrayList<String> getDiedRolls() {
        ArrayList<String> diedRolls = new ArrayList<>();
        for (Player player : players) {
            if (!player.isAlive()) {
                diedRolls.add(player.getRoll());
            }
        }
        return diedRolls;
    }
}
