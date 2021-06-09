//package com.company;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//public class ManageData {
//    private static ManageData manageData = null;
//    private  HashMap<Player, Handler> playerHandler = new HashMap<>();
//    private  ArrayList<String> rolls = new ArrayList<>();
//    private ArrayList<String> userNames = new ArrayList<>();
//
//    private ManageData() {
//
//    }
//
//    public static ManageData getInstance() {
//        if (manageData == null)
//            manageData = new ManageData();
//        return manageData;
//
//    }
//
//
//    public synchronized void printUserNames() {
//        int counter = 1;
//        for (String userName : userNames) {
//            System.out.println(counter + ")" + userName);
//            counter++;
//        }
//
//    }
//
//    public void addUserName(String userName) {
//        userNames.add(userName);
//    }
//
//    public  void addPlayerHandler(Player player, Handler handler) {
//        playerHandler.put(player, handler);
//    }
//
//    public  void setUserNames(ArrayList<String> userNames) {
//        this.userNames = userNames;
//    }
//
//}
