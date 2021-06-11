package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GameManger {
    private static ArrayList<String> rolls;
    private int numberOfPlayers;
    private int numberOfMafiaPlayers;
    private Server server;
    private ShareData shareData;
    private static ArrayList<String> readyUsers = new ArrayList<>();

    public GameManger(Server server, ShareData shareData, int numberOfPlayers) {
        String[] rollArray = new String[]{"Godfather", "Dr.Lecter", "Simple Mafia", "City Doctor"
                , "Simple Citizen", "Detective", "Diehard", "Psychologist", "Professional", "Mayor"};
        rolls = new ArrayList<>(Arrays.asList(rollArray));
        this.server = server;
        this.shareData = shareData;
        this.numberOfPlayers = numberOfPlayers;
        this.numberOfMafiaPlayers = numberOfPlayers / 3;
    }


//    public void startGame() {
//        ArrayList<Player> players = new ArrayList<>(playerHandler.keySet());
//        ArrayList<Handler> handlers = new ArrayList<>(playerHandler.values());
//
//        int[] array = new int[userNames.size()];
//        for (Handler handler : handlers) {
//            handler.sendMessage("Vote bede");
////            handler.sendString(getUserNamesString(handler));
//        }
//
//        for (Handler handler : handlers) {
//            int vote = Integer.parseInt(handler.readMessage());
//            System.out.println(vote);
//            if (vote != -1) {
//                array[vote]++;
//                System.out.println(array[vote]);
//            }
//
//        }
//        int counter = -1;
//        for (int i = 0; i < array.length; i++) {
//            if (array[i] > counter) {
//                counter = i;
//            }
//        }
//        if (counter != -1) {
//            shareData.findPlayerByUserName(userNames.get(counter)).setState(false);
//            System.out.println(userNames.get(counter));
//        }
//        for (Handler handler : handlers) {
//            handler.sendMessage("Vote tamam");
//        }
//        for (Player player : players) {
//            playerHandler.get(player).sendObject(player.getInstance(player));
//        }
//
//
//    }


//    public boolean checkEndOfGame() {
//        ArrayList<Player> players = new ArrayList<>(playerHandler.keySet());
//        int mafiaNumber = 0;
//        int citizenNumber = 0;
//        for (Player player : players) {
//            if (player instanceof MafiaPlayer) {
//                mafiaNumber++;
//            } else if (player instanceof CitizenPlayer) {
//                citizenNumber++;
//            }
//        }
//        if (mafiaNumber >= citizenNumber || mafiaNumber == 0) {
//            return true;
//        }
//        return false;
//    }


    public void introductionNight() {
        ArrayList<Handler> handlers = new ArrayList<>(server.getPlayerHandler().values());
        server.broadcast("Introduction night",null);

        for (Handler handler : handlers){
            handler.sendObject(shareData);
        }
        for (Handler handler : handlers){
            handler.sendMessage("Your roll:" + handler.getPlayer().getRoll());
        }

        ArrayList<Player> mafiaPlayers = new ArrayList<>();
        ArrayList<Player> players = new ArrayList<>(server.getPlayerHandler().keySet());
        for (Player player : players) {
            if (player instanceof MafiaPlayer) {
                mafiaPlayers.add(player);
            }
        }
        for (Handler handler : handlers) {
            if (handler.getPlayer() instanceof Mayor){
                Player player = shareData.findPlayerByRoll("City Doctor");
               if (player != null){
                   handler.sendMessage(player.getUserName() +" :City Doctor");
               }
            }
            else if (handler.getPlayer() instanceof MafiaPlayer) {
                for (Player mafiaPlayer : mafiaPlayers) {
                    if (mafiaPlayer != handler.getPlayer()) {
                        handler.sendMessage(mafiaPlayer.getUserName() + ":" + mafiaPlayer.getRoll());
                    }
                }
            }
        }
        server.broadcast("End introduction night",null);

    }
    public void voting(){
        ArrayList<Handler> handlers = new ArrayList<>(server.getPlayerHandler().values());
        int[] array = new int[shareData.getNumberOfAlivePlayer()];
        server.broadcast("Start voting",null);
        for (Handler handler : handlers) {
            int vote = Integer.parseInt(handler.readMessage());
            if (vote != -1) {
                array[vote - 1]++;
                System.out.println(array[vote]);
            }
        }
        int value = 0;
        int maxVote = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > value) {
                value = array[i];
                maxVote = i;
            }
        }
        if (maxVote != -1) {
            shareData.findPlayerByUserName(shareData.getUserName(maxVote)).setState(false);
            System.out.println(shareData.getUserName(maxVote));
        }

        for (Player player : players) {
            playerHandler.get(player).sendObject(player.getInstance(player));
        }
        server.broadcast("End voting",null);
    }


    public static Player setRoll(String userName) {

        int randomValue = new Random().nextInt(rolls.size());
        Player player = new PlayerFactory().getPlayer(rolls.get(randomValue), userName);
        rolls.remove(randomValue);
        return player;

    }

    public static void addReadyUser(String userName) {
        readyUsers.add(userName);

    }

    public static ArrayList<String> getReadyUsers() {
        return readyUsers;
    }
}
