package com.company;

import javax.imageio.IIOException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ShareData implements Serializable,Cloneable {

//    private ArrayList<String> rolls;
    private ArrayList<String> userNames;
   private ArrayList<Player> players;

    public ShareData() {
//        rolls = new ArrayList<>();
        userNames = new ArrayList<>();
        players = new ArrayList<>();

    }

    public ShareData(ArrayList<Player> players, ArrayList<String> userNames) {
//        this.rolls = rolls;
        this.userNames = userNames;
        this.players = players;

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
            if (!u.equals(userName) && findPlayerByUserName(u).isAlive()) {
                System.out.println(counter + ")" + u + findPlayerByUserName(u));
                counter++;
            }
        }
    }
    public synchronized ArrayList<String> getOthersUserNames(String userName){
     ArrayList<String> userNames = new ArrayList<>();
        for (String u : this.userNames) {
            if (!u.equals(userName) && findPlayerByUserName(u).isAlive()) {
               userNames.add(u);
            }
        }
        return userNames;
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
            if (player.isAlive()) {
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

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ArrayList<Player> players = this.players;
        ArrayList<Player> p = new ArrayList<>();
            for (Player player : players) {
                p.add((Player) player.clone());
            }
        return new ShareData(p,this.userNames);
    }

    public ShareData create2 (ArrayList<Player> players, ArrayList<String> userNames){
        ArrayList<Player> p = (ArrayList<Player>) players.clone();
//        ArrayList<String> u = new ArrayList<>();

//            try {
//                for (Player player : players) {
//                    p.add((Player) player.clone());
//                }
//                for (String userName : userNames){
//                    u.add(userName.toString());
//                }
//            } catch (CloneNotSupportedException e) {
//                e.printStackTrace();
//            }
            return new ShareData(p,userNames);
    }


//    public ArrayList<String> getRolls() {
//        return rolls;
//    }
}
