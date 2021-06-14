package com.company;

import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class GameManger {
    private static ArrayList<String> rolls;
    private static int numberOfPlayers;
    private Server server;
    private ShareData shareData;
    private boolean isInquiry;
    private ArrayList<String> lastNightDeadUserNames;

    public GameManger(Server server, ShareData shareData) {
        this.server = server;
        this.shareData = shareData;
        isInquiry = false;
        lastNightDeadUserNames = new ArrayList<>();
    }


    public void startGame(){

        introductionNight();
        server.broadcast("Start day",null);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        server.notifyHandlers();
        try {
            synchronized (server.getThread()) {
                server.getThread().wait();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (!checkEndOfGame()) {
            voting();
            server.broadcast("Start day",null);
//            voting();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            server.notifyHandlers();
            try {
                synchronized (server.getThread()) {
                    server.getThread().wait();
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }


    public boolean checkEndOfGame() {
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
        return false;
    }


    public void introductionNight() {
        ArrayList<Handler> handlers = new ArrayList<>(server.getPlayerHandler().values());
        server.broadcast("Introduction night", null);
        sendTheLatestAliveUsersList();
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
                    handler.sendMessage(player.getUserName() + ":City Doctor");
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
//        for (Handler handler : handlers) {
//            try {
//                handler.sendObject(handler.getPlayer().clone());
//            } catch (CloneNotSupportedException e) {
//                e.printStackTrace();
//            }
//        }
        sendTheLatestAliveUsersList();


        for (Handler handler : handlers) {
            String vote = handler.readMessage();
            if (!vote.equals("null")) {
//                server.broadcast(handler.getUserName() + " voted:" + vote,handler);
                if (userNameVotes.get(vote) != null) {
                    userNameVotes.put(vote, userNameVotes.get(vote) + 1);
                } else {
                    userNameVotes.put(vote, 0);
                }
            }
        }
        server.broadcast("End voting", null);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mayor mayor = (Mayor) shareData.findPlayerByRoll("Mayor");
        boolean continueVoting = true;
        if (mayor != null && mayor.getStateAbility() && mayor.isAlive()) {
            Handler MayorHandler = server.getHandler(mayor);
            String decision = MayorHandler.readMessage();
            if (decision.equals("Yes")) {
                continueVoting = false;
                mayor.setStateAbility(false);
            }
        }
        Player diePlayer = null;
        if (continueVoting) {
            int maxVoteValue = 0;
            String maxVoteUserName = null;
            ArrayList<String> userNames = shareData.getUserNames();
            for (String userName : userNames) {
                if (userNameVotes.get(userName) != null) {
                    if (userNameVotes.get(userName) > maxVoteValue) {
                        maxVoteValue = userNameVotes.get(userName);
                        maxVoteUserName = userName;
                    }
                }
            }
            boolean votingResult = true;
            for (String userName : userNames) {
                if (userNameVotes.get(userName) != null) {
                    if (userNameVotes.get(userName) == maxVoteValue && !userName.equals(maxVoteUserName)) {
                        votingResult = false;
                    }
                }
            }
            if (votingResult) {
                if (maxVoteUserName != null) {
                    diePlayer = shareData.findPlayerByUserName(maxVoteUserName);
                    diePlayer.setAlive(false);
                }
            }
        }
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        try {
            for (Handler handler : handlers) {
                handler.sendObject(handler.getPlayer().clone());
            }
            sendTheLatestAliveUsersList();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        if (diePlayer != null) {
            server.broadcast(diePlayer.getUserName() + ":died", server.getHandler(diePlayer));
            server.getHandler(diePlayer).sendMessage("You die");
            server.getHandler(diePlayer).exitGame();
        } else {
            server.broadcast("Nobody die",null);
        }


    }


    public static void setRolls() {
        String[] rollArray = new String[]{"Godfather", "Dr.Lecter", "City Doctor"
                , "Detective", "Diehard", "Psychologist", "Professional", "Mayor"};
        rolls = new ArrayList<>(Arrays.asList(rollArray));
        if (numberOfPlayers > 8) {
            for (int i = 0; i < (numberOfPlayers / 3) - 2; i++) {
                rolls.add("Simple Mafia");
            }
            for (int i = 0; i < numberOfPlayers - rolls.size(); i++) {
                rolls.add("Simple Citizen");
            }
        }
    }

    public static Player getRoll(String userName) {
        int randomValue = new Random().nextInt(rolls.size());
        Player player = new PlayerFactory().getPlayer(rolls.get(randomValue), userName);
        rolls.remove(randomValue);
        return player;

    }

    public void nightGame() {
        server.broadcast("Start night", null);


        if (shareData.findPlayerByRoll("God father").isAlive()) {
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
            if (diePlayer != null) {
                diePlayer.setAlive(false);
                lastNightDeadUserNames.add(diePlayer.getUserName());
            }
        }
        if (shareData.findPlayerByRoll("Dr.lecter").isAlive()) {
            DoctorLecter doctorLecter = (DoctorLecter) shareData.findPlayerByRoll("Dr.lecter");
            Handler doctorLecterHandler = server.getHandler(doctorLecter);
            if (!shareData.findPlayerByRoll("God father").isAlive()) {
                SimpleMafia simpleMafia = (SimpleMafia) shareData.findPlayerByRoll("Simple Mafia");
                doctorLecterHandler.sendMessage("Kill");
                doctorLecterHandler.sendObject(shareData.getMafiaPlayersUserNames());
                doctorLecterHandler.sendObject(shareData.getCitizenPlayersUserNames());
                server.getHandler(simpleMafia).sendMessage("Get vote");
                doctorLecterHandler.sendMessage(server.getHandler(simpleMafia).readMessage());
                Player diePlayer = shareData.findPlayerByUserName(doctorLecterHandler.readMessage());
                if (diePlayer != null) {
                    diePlayer.setAlive(false);
                    lastNightDeadUserNames.add(diePlayer.getUserName());
                }
            }
            doctorLecterHandler.sendMessage("Act");
            doctorLecterHandler.sendObject(shareData.getMafiaPlayersUserNames());
            Player hillPlayer = shareData.findPlayerByUserName(server.getHandler(doctorLecter).readMessage());
            if (hillPlayer != null) {
                MafiaPlayer hillPLayer = (MafiaPlayer) hillPlayer;
                hillPLayer.setHill(true);
            }
        }
        if (shareData.findPlayerByRoll("Simple Mafia").isAlive()) {
            SimpleMafia simpleMafia = (SimpleMafia) shareData.findPlayerByRoll("Simple Mafia");
            Handler simpleMafiaHandler = server.getHandler(simpleMafia);
            if (!shareData.findPlayerByRoll("God father").isAlive() && !shareData.findPlayerByRoll("Dr.lecter").isAlive()) {
                simpleMafiaHandler.sendMessage("Kill");
                simpleMafiaHandler.sendObject(shareData.getMafiaPlayersUserNames());
                simpleMafiaHandler.sendObject(shareData.getCitizenPlayersUserNames());
                Player diePlayer = shareData.findPlayerByUserName(simpleMafiaHandler.readMessage());
                if (diePlayer != null) {
                    diePlayer.setAlive(false);
                    lastNightDeadUserNames.add(diePlayer.getUserName());
                }
            }
        }
        if (shareData.findPlayerByRoll("City Doctor").isAlive()) {
            CityDoctor cityDoctor = (CityDoctor) shareData.findPlayerByRoll("City Doctor");
            server.getHandler(cityDoctor).sendMessage("Act");
            Player hillPlayer = shareData.findPlayerByUserName(server.getHandler(cityDoctor).readMessage());
            if (!hillPlayer.isAlive()) {
                if (hillPlayer instanceof CitizenPlayer) {
                    if (hillPlayer instanceof CityDoctor) {
                        cityDoctor.setStateAbility(false);
                    }
                    hillPlayer.setAlive(true);
                    if (lastNightDeadUserNames.contains(hillPlayer.getUserName())) {
                        lastNightDeadUserNames.remove(hillPlayer.getUserName());
                    }

                }
            }
        }

        if (shareData.findPlayerByRoll("Detective").isAlive()) {
            Detective detective = (Detective) shareData.findPlayerByRoll("Detective");
            server.getHandler(detective).sendMessage("Act");
            Player player = shareData.findPlayerByUserName(server.getHandler(detective).readMessage());
            if (player instanceof MafiaPlayer) {
                if (!(player instanceof GodFather)) {
                    server.getHandler(detective).sendMessage("MafiaPlayer");
                } else
                    server.getHandler(detective).sendMessage("CitizenPlayer");
            } else
                server.getHandler(detective).sendMessage("CitizenPlayer");
        }
        if (shareData.findPlayerByRoll("Professional").isAlive()) {
            Professional professional = (Professional) shareData.findPlayerByRoll("Professional");
            server.getHandler(professional).sendMessage("Act");
            Player player = shareData.findPlayerByUserName(server.getHandler(professional).readMessage());
            if (player != null) {
                if (player instanceof CitizenPlayer) {
                    professional.setAlive(false);
                    lastNightDeadUserNames.add(professional.getUserName());
                } else {
                    MafiaPlayer mafiaPlayer = (MafiaPlayer) player;
                    if (mafiaPlayer.isHill()) {
                        mafiaPlayer.setHill(false);
                        if (mafiaPlayer instanceof DoctorLecter) {
                            DoctorLecter doctorLecter = (DoctorLecter) mafiaPlayer;
                            doctorLecter.setStateAbility(false);
                            lastNightDeadUserNames.add(doctorLecter.getUserName());
                        }
                    } else mafiaPlayer.setHill(false);
                    lastNightDeadUserNames.add(mafiaPlayer.getUserName());
                }

            }
        }
        if (shareData.findPlayerByRoll("Psychologist").isAlive()) {
            Psychologist psychologist = (Psychologist) shareData.findPlayerByRoll("Psychologist");
            server.getHandler(psychologist).sendMessage("Act");
            Player player = shareData.findPlayerByUserName(server.getHandler(psychologist).readMessage());
            if (player != null) {
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

    public Player getSilentPlayer() {
        ArrayList<Player> players = shareData.getPlayers();
        for (Player player : players) {
            if (player.isAlive() && player.isSilent()) {
                return player;
            }
        }
        return null;
    }

    public static void setNumberOfPlayers(int numberOfPlayers) {
        GameManger.numberOfPlayers = numberOfPlayers;
    }
    public void sendTheLatestAliveUsersList(){
        ArrayList<Player> players = shareData.getPlayers();
        ArrayList<Handler> handlers = new ArrayList<>(server.getPlayerHandler().values());
        for (Handler handler : handlers){
            handler.sendValue(shareData.getNumberOfAlivePlayer());
            for (Player player : players){
                if (player.isAlive()){
                    handler.sendMessage(player.getUserName());
                }
            }
        }
    }
}
