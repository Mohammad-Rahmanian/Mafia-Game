package com.company;

import java.util.ArrayList;


/**
 * This class stores and shares data.
 *
 * @author Mohammad Rahmanian.
 * @version 1.0
 */
public class ShareData {

    private ArrayList<String> userNames;
    private ArrayList<Player> players;


    /**
     * Instantiates a new Share data.
     */
    public ShareData() {
        userNames = new ArrayList<>();
        players = new ArrayList<>();

    }


    /**
     * Gets mafia players usernames.
     *
     * @return the mafia players usernames.
     */
    public ArrayList<String> getMafiaPlayersUserNames() {
        ArrayList<String> mafiaPlayersUserNames = new ArrayList<>();
        for (Player player : players) {
            if (player.isAlive() && player instanceof MafiaPlayer) {
                mafiaPlayersUserNames.add(player.getUserName());
            }
        }
        return mafiaPlayersUserNames;
    }

    /**
     * Gets citizen players usernames.
     *
     * @return the citizen players usernames
     */
    public ArrayList<String> getCitizenPlayersUserNames() {
        ArrayList<String> citizenPlayerUserNames = new ArrayList<>();
        for (Player player : players) {
            if (player.isAlive() && player instanceof CitizenPlayer) {
                citizenPlayerUserNames.add(player.getUserName());
            }
        }
        return citizenPlayerUserNames;
    }


    /**
     * Checks that the username is not duplicate.
     *
     * @param userName the user name to be checked.
     * @return true if username is not duplicate.
     */
    public boolean checkUserName(String userName) {
        for (String u : userNames) {
            if (u.equals(userName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Find player by username.
     *
     * @param userName the user name of the player.
     * @return the player.
     */
    public Player findPlayerByUserName(String userName) {
        for (Player player : players) {
            if (player.getUserName().equals(userName)) {
                return player;
            }
        }
        return null;
    }


    /**
     * Add user name.
     *
     * @param userName the user name to be added.
     */
    public void addUserName(String userName) {
        userNames.add(userName);
    }


    /**
     * Gets user names.
     *
     * @return the user names.
     */
    public ArrayList<String> getUserNames() {
        return userNames;
    }

    /**
     * Add player.
     *
     * @param player the player to be added.
     */
    public void addPlayer(Player player) {
        players.add(player);
    }

    /**
     * Gets players.
     *
     * @return the players
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Find player by roll.
     *
     * @param roll the roll of the player.
     * @return the player.
     */
    public Player findPlayerByRoll(String roll) {
        for (Player player : players) {
            if (player.getRoll().equals(roll)) {
                return player;
            }
        }
        return null;
    }

    /**
     * Gets number of alive player.
     *
     * @return the number of alive player.
     */
    public int getNumberOfAlivePlayer() {
        int numberOfAlivePlayer = 0;
        for (Player player : players) {
            if (player.isAlive()) {
                numberOfAlivePlayer++;
            }
        }
        return numberOfAlivePlayer;
    }

    /**
     * Gets died rolls.
     *
     * @return the died rolls.
     */
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
