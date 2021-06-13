package com.company;

import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class GameManger {
    private static ArrayList<String> rolls;
    private int numberOfPlayers;
    private int numberOfMafiaPlayers;
    private Server server;
    private ShareData shareData;
    private static ArrayList<String> readyUsers = new ArrayList<>();
    private boolean isInquiry;
    private ArrayList<String> lastNightDeadUserNames;

    public GameManger(Server server, ShareData shareData, int numberOfPlayers) {
        String[] rollArray = new String[]{"Godfather", "Dr.Lecter", "Simple Mafia", "City Doctor"
                , "Simple Citizen", "Detective", "Diehard", "Psychologist", "Professional", "Mayor"};
        rolls = new ArrayList<>(Arrays.asList(rollArray));
        this.server = server;
        this.shareData = shareData;
        this.numberOfPlayers = numberOfPlayers;
        this.numberOfMafiaPlayers = numberOfPlayers / 3;
        isInquiry = false;
        lastNightDeadUserNames = new ArrayList<>();
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
        server.broadcast("Introduction night", null);

        for (Handler handler : handlers) {
            handler.sendObject(shareData);
        }
        for (Handler handler : handlers) {
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
            if (handler.getPlayer() instanceof Mayor) {
                Player player = shareData.findPlayerByRoll("City Doctor");
                if (player != null) {
                    handler.sendMessage(player.getUserName() + " :City Doctor");
                }
            } else if (handler.getPlayer() instanceof MafiaPlayer) {
                for (Player mafiaPlayer : mafiaPlayers) {
                    if (mafiaPlayer != handler.getPlayer()) {
                        handler.sendMessage(mafiaPlayer.getUserName() + ":" + mafiaPlayer.getRoll());
                    }
                }
            }
        }
        server.broadcast("End introduction night", null);

    }

    public void voting() {
        ArrayList<Handler> handlers = new ArrayList<>(server.getPlayerHandler().values());
        HashMap<String, Integer> userNameVotes = new HashMap<>();
        server.broadcast("Start voting", null);
        for (Handler handler : handlers) {
            try {
                handler.sendObject(handler.getPlayer().clone());
                handler.sendObject(shareData.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }



        for (Handler handler : handlers) {
            String vote = handler.readMessage();
            if (!vote.equals("null")) {
                if (userNameVotes.get(vote) != null) {
                    userNameVotes.put(vote, userNameVotes.get(vote) + 1);
                } else {
                    userNameVotes.put(vote, 0);
                }
            }
        }
        server.broadcast("End voting", null);
        Mayor mayor =(Mayor) shareData.findPlayerByRoll("Mayor");
        boolean continueVoting = true;
        if (mayor != null && mayor.getStateAbility()) {
            Handler handler = server.getHandler(mayor);
            handler.sendMessage("Do you want to cancel the vote?");
            String decision = handler.readMessage();
            System.out.println(decision);
            if (decision.equals("Yes")) {
                continueVoting = false;
                mayor.setStateAbility(false);
            }
//            else if (decision.equals("No")) {
//            }
        }
        Player diePlayer = null;
        if (continueVoting) {
            System.out.println("Ah");
            int maxVoteValue = 0;
            String maxVoteUserName = null;
            ArrayList<String> userNames = shareData.getUserNames();
            for (String userName : userNames) {
                if (userNameVotes.get(userName) != null) {
                    System.out.println("llllllll");
                    if (userNameVotes.get(userName) > maxVoteValue) {
                        System.out.println("qqqqqqqq");
                        maxVoteValue = userNameVotes.get(userName);
                        maxVoteUserName = userName;
                        System.out.println(maxVoteUserName);
                    }
                }
            }
            System.out.println( userNameVotes.toString());
            boolean votingResult = true;
            for (String userName : userNames) {
                if (userNameVotes.get(userName) != null) {
                    if (userNameVotes.get(userName) == maxVoteValue && !userName.equals(maxVoteUserName)) {
                        System.out.println("kkkk");
                        votingResult = false;
                    }
                }
            }
            if (votingResult) {
                if (maxVoteUserName != null) {
                    System.out.println("aaa");
                    diePlayer = shareData.findPlayerByUserName(maxVoteUserName);
                    diePlayer.setAlive(false);
                }
            }
        }


        try {
            for (Handler handler : handlers) {
                handler.sendObject(handler.getPlayer().clone());
                handler.sendObject(shareData.clone());
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        if (diePlayer != null) {
            server.broadcast(diePlayer.getUserName() + ":died",server.getHandler(diePlayer));
            server.getHandler(diePlayer).sendMessage("You die");
            server.getHandler(diePlayer).exitGame();
        }
        else {
            System.out.println("Nobody die");
        }


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

    public void nightGame(){
        server.broadcast("Start night",null);


        if (shareData.findPlayerByRoll("God father").isAlive()){
            GodFather godFather = (GodFather) shareData.findPlayerByRoll("God father");
            DoctorLecter doctorLecter = (DoctorLecter) shareData.findPlayerByRoll("Dr.lecter");
            SimpleMafia simpleMafia = (SimpleMafia) shareData.findPlayerByRoll("Simple Mafia");
            Handler godFatherHandler = server.getHandler(godFather);
            godFatherHandler.sendMessage("Act");
            godFatherHandler.sendObject(shareData.getMafiaPlayersUserNames());
            godFatherHandler.sendObject(shareData.getCitizenPlayersUserNames());
            server.getHandler(doctorLecter).sendMessage("Get vote");
            server.getHandler(simpleMafia).sendMessage("Get vote");
            godFatherHandler.sendMessage(server.getHandler(doctorLecter).readMessage());
            godFatherHandler.sendMessage(server.getHandler(simpleMafia).readMessage());
            Player diePlayer = shareData.findPlayerByUserName(godFatherHandler.readMessage());
            if (diePlayer != null){
                diePlayer.setAlive(false);
                lastNightDeadUserNames.add(diePlayer.getUserName());
            }
        }
        if (shareData.findPlayerByRoll("Dr.lecter").isAlive()){
            DoctorLecter doctorLecter = (DoctorLecter) shareData.findPlayerByRoll("Dr.lecter");
            Handler doctorLecterHandler = server.getHandler(doctorLecter);
            if (!shareData.findPlayerByRoll("God father").isAlive()){
                SimpleMafia simpleMafia = (SimpleMafia) shareData.findPlayerByRoll("Simple Mafia");
                doctorLecterHandler.sendMessage("Kill");
                doctorLecterHandler.sendObject(shareData.getMafiaPlayersUserNames());
                doctorLecterHandler.sendObject(shareData.getCitizenPlayersUserNames());
                server.getHandler(simpleMafia).sendMessage("Get vote");
                doctorLecterHandler.sendMessage(server.getHandler(simpleMafia).readMessage());
                Player diePlayer = shareData.findPlayerByUserName(doctorLecterHandler.readMessage());
                if (diePlayer != null){
                    diePlayer.setAlive(false);
                    lastNightDeadUserNames.add(diePlayer.getUserName());
                }
            }
            doctorLecterHandler.sendMessage("Act");
            doctorLecterHandler.sendObject(shareData.getMafiaPlayersUserNames());
            Player hillPlayer = shareData.findPlayerByUserName(server.getHandler(doctorLecter).readMessage());
            if (hillPlayer != null){
                MafiaPlayer hillPLayer = (MafiaPlayer) hillPlayer;
                hillPLayer.setHill(true);
            }
        }
        if (shareData.findPlayerByRoll("Simple Mafia").isAlive()){
            SimpleMafia simpleMafia = (SimpleMafia) shareData.findPlayerByRoll("Simple Mafia");
            Handler simpleMafiaHandler = server.getHandler(simpleMafia);
            if (!shareData.findPlayerByRoll("God father").isAlive() && !shareData.findPlayerByRoll("Dr.lecter").isAlive()){
                simpleMafiaHandler.sendMessage("Kill");
                simpleMafiaHandler.sendObject(shareData.getMafiaPlayersUserNames());
                simpleMafiaHandler.sendObject(shareData.getCitizenPlayersUserNames());
                Player diePlayer = shareData.findPlayerByUserName(simpleMafiaHandler.readMessage());
                if (diePlayer != null){
                    diePlayer.setAlive(false);
                    lastNightDeadUserNames.add(diePlayer.getUserName());
                }
            }
        }
        if (shareData.findPlayerByRoll("City Doctor").isAlive()){
            CityDoctor cityDoctor = (CityDoctor) shareData.findPlayerByRoll("City Doctor");
            server.getHandler(cityDoctor).sendMessage("Act");
            Player hillPlayer = shareData.findPlayerByUserName(server.getHandler(cityDoctor).readMessage());
            if (!hillPlayer.isAlive()){
                if (hillPlayer instanceof CitizenPlayer){
                    if (hillPlayer instanceof CityDoctor){
                        cityDoctor.setStateAbility(false);
                    }
                    hillPlayer.setAlive(true);
                    if (lastNightDeadUserNames.contains(hillPlayer.getUserName())){
                        lastNightDeadUserNames.remove(hillPlayer.getUserName());
                    }

                }
            }
        }

        if (shareData.findPlayerByRoll("Detective").isAlive()){
            Detective detective = (Detective) shareData.findPlayerByRoll("Detective");
            server.getHandler(detective).sendMessage("Act");
            Player player = shareData.findPlayerByUserName(server.getHandler(detective).readMessage());
            if (player instanceof MafiaPlayer){
                if (!(player instanceof GodFather)){
                    server.getHandler(detective).sendMessage("MafiaPlayer");
                }
                else
                    server.getHandler(detective).sendMessage("CitizenPlayer");
            }
            else
                server.getHandler(detective).sendMessage("CitizenPlayer");
        }
        if (shareData.findPlayerByRoll("Professional").isAlive()){
            Professional professional = (Professional) shareData.findPlayerByRoll("Professional");
            server.getHandler(professional).sendMessage("Act");
            Player player = shareData.findPlayerByUserName(server.getHandler(professional).readMessage());
            if (player != null){
                if (player instanceof CitizenPlayer){
                    professional.setAlive(false);
                    lastNightDeadUserNames.add(professional.getUserName());
                }
                else{
                    MafiaPlayer mafiaPlayer = (MafiaPlayer) player;
                    if (mafiaPlayer.isHill()){
                        mafiaPlayer.setHill(false);
                        if (mafiaPlayer instanceof DoctorLecter){
                            DoctorLecter doctorLecter =(DoctorLecter) mafiaPlayer;
                            doctorLecter.setStateAbility(false);
                            lastNightDeadUserNames.add(doctorLecter.getUserName());
                        }
                    }
                    else mafiaPlayer.setHill(false);
                    lastNightDeadUserNames.add(mafiaPlayer.getUserName());
                }

            }
        }
        if (shareData.findPlayerByRoll("Psychologist").isAlive()){
            Psychologist psychologist = (Psychologist) shareData.findPlayerByRoll("Psychologist");
            server.getHandler(psychologist).sendMessage("Act");
            Player player = shareData.findPlayerByUserName(server.getHandler(psychologist).readMessage());
            if (player != null){
                player.setSilent(true);
            }
        }
        if (shareData.findPlayerByRoll("Diehard").isAlive()) {
            DieHard dieHard = (DieHard) shareData.findPlayerByRoll("Diehard");
            server.getHandler(dieHard).sendMessage("Act");
            if (dieHard.getInquiryNumber() != 0) {
                if (server.getHandler(dieHard).readMessage().equals("Yes")) {
                    isInquiry = true;
                    dieHard.setInquiryNumber(dieHard.getInquiryNumber() - 1);
                }
            }
        }

    }
    public Player getSilentPlayer(){
        ArrayList<Player> players =shareData.getPlayers();
        for (Player player : players){
            if (player.isAlive() && player.isSilent()){
                return player;
            }
        }
        return null;
    }
}
