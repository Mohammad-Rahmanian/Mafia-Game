package com.company;

import java.util.ArrayList;
import java.util.HashMap;

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

    public void addUserName(String userName) {
        userNames.add(userName);
    }

    public  void addPlayerHandler(Player player, Handler handler) {
        playerHandler.put(player, handler);
    }

    public  HashMap<Player, Handler> getPlayerHandler() {
        return playerHandler;
    }

}
