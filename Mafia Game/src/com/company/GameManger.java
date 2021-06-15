package com.company;

import java.util.*;

/**
 * This class manages the game.
 *
 * @author Mohammad Rahmanian.
 * @version 1.0
 */
public class GameManger {
    private static ArrayList<String> rolls;
    private static int numberOfPlayers;
    private Server server;
    private ShareData shareData;
    private boolean isInquiry;
    private ArrayList<String> lastNightDeadUserNames;

    /**
     * Instantiates a new Game manger.
     *
     * @param server    the server.
     * @param shareData the share data.
     */
    public GameManger(Server server, ShareData shareData) {
        this.server = server;
        this.shareData = shareData;
        isInquiry = false;
        lastNightDeadUserNames = new ArrayList<>();
    }

    /**
     * Start game.
     */
    public void startGame() {
        introductionNight();
        while (!checkEndOfGame()) {
            startDay();
            voting();
            nightGame();
        }
        System.out.println("End MafiaGame.");
        server.broadcast("End match.", null);
        if (shareData.getMafiaPlayersUserNames().size() == 0) {
            server.broadcast("Citizen players won", null);
        } else {
            server.broadcast("Mafia players won", null);
        }

    }

    /**
     * Introduction night.
     */
    public void introductionNight() {
        ArrayList<Handler> handlers = new ArrayList<>(server.getPlayerHandler().values());
        server.broadcast("Start introduction night:", null);
        sendTheLatestAliveUsersList();
        for (Handler handler : handlers) {
            handler.sendMessage("Your roll is " + handler.getPlayer().getRoll());
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
                    handler.sendMessage(player.getUserName() + " is City Doctor");
                }
            } else if (handler.getPlayer() instanceof MafiaPlayer) {
                for (Player mafiaPlayer : mafiaPlayers) {
                    if (mafiaPlayer != handler.getPlayer()) {
                        handler.sendMessage(mafiaPlayer.getUserName() + " is " + mafiaPlayer.getRoll());
                    }
                }
            }
        }
        server.broadcast("End introduction night", null);
    }

    /**
     * Start day.
     */
    public void startDay() {
        try {
            server.broadcast("Start day:", null);
            ArrayList<Handler> handlers = new ArrayList<>(server.getPlayerHandler().values());
            sendTheLatestAliveUsersList();
            for (Handler handler : handlers) {
                if (lastNightDeadUserNames.contains(handler.getUserName())) {
                    handler.exitGame();
                }
            }
            for (String userName : lastNightDeadUserNames) {
                server.broadcast(userName + " died last night.", null);
            }
            lastNightDeadUserNames.clear();
            if (getSilentPlayer() != null) {
                server.broadcast(getSilentPlayer().getUserName() + " is silent today.", null);
            }
            if (isInquiry) {
                ArrayList<String> diedRolls = shareData.getDiedRolls();
                server.broadcast("DieHard has inquired.", null);
                if (diedRolls.isEmpty()) {
                    server.broadcast("No role is dead", null);
                } else {
                    for (String roll : diedRolls) {
                        server.broadcast(roll + " is dead", null);
                    }
                }
                isInquiry = false;
            }
            Thread.sleep(2000);
            server.notifyHandlers();
            synchronized (server.getThread()) {
                server.getThread().wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        server.broadcast("End day:", null);
    }

    /**
     * Voting.
     */
    public void voting() {
        ArrayList<Handler> handlers = new ArrayList<>(server.getPlayerHandler().values());
        HashMap<String, Integer> userNameVotes = new HashMap<>();
        server.broadcast("Start voting", null);
        sendTheLatestAliveUsersList();
        for (Handler handler : handlers) {
            if (handler.getPlayer().isAlive()) {
                String vote = handler.readMessage();
                if (!vote.equals("null")) {
                    server.broadcast(handler.getUserName() + " voted " + vote, handler);
                    if (userNameVotes.get(vote) != null) {
                        userNameVotes.put(vote, userNameVotes.get(vote) + 1);
                    } else {
                        userNameVotes.put(vote, 0);
                    }
                } else {
                    server.broadcast(handler.getUserName() + " did not vote for anyone", handler);
                }
            }
        }
        Mayor mayor = (Mayor) shareData.findPlayerByRoll("Mayor");
        boolean continueVoting = true;
        if (mayor != null) {
            if (mayor.getStateAbility() && mayor.isAlive()) {
                Handler mayorHandler = server.getHandler(mayor);
                String decision = mayorHandler.readMessage();
                if (decision.equals("Yes")) {
                    continueVoting = false;
                    mayor.setStateAbility(false);
                }
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
                    diePlayer.kill();
                }
            }
        }
        try {
            for (Handler handler : handlers) {
                handler.sendObject(handler.getPlayer().clone());
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        sendTheLatestAliveUsersList();
        if (diePlayer != null) {
            server.broadcast(diePlayer.getUserName() + " died", server.getHandler(diePlayer));
            server.getHandler(diePlayer).sendMessage("You die.");
            server.getHandler(diePlayer).exitGame();
        } else {
            server.broadcast("Nobody died.", null);
        }


    }

    /**
     * Night game.
     */
    public void nightGame() {
        server.broadcast("Start night", null);
        if (shareData.findPlayerByRoll("Godfather") != null) {
            GodFather godFather = (GodFather) shareData.findPlayerByRoll("Godfather");
            if (godFather.isAlive()) {
                DoctorLecter doctorLecter = (DoctorLecter) shareData.findPlayerByRoll("Dr.Lecter");
                SimpleMafia simpleMafia = (SimpleMafia) shareData.findPlayerByRoll("Simple Mafia");
                Handler godFatherHandler = server.getHandler(godFather);
                server.broadcast("Mafia wakes up.", null);
                godFatherHandler.sendMessage("Act");
                godFatherHandler.sendValue(shareData.getMafiaPlayersUserNames().size());
                ArrayList<String> citizenPlayersUserNames = shareData.getCitizenPlayersUserNames();
                godFatherHandler.sendValue(citizenPlayersUserNames.size());
                for (String userName : citizenPlayersUserNames) {
                    godFatherHandler.sendMessage(userName);
                }
                if (doctorLecter != null) {
                    if (doctorLecter.isAlive()) {
                        Handler doctorLecterHandler = server.getHandler(doctorLecter);
                        doctorLecterHandler.sendMessage("Get vote");
                        doctorLecterHandler.sendValue(citizenPlayersUserNames.size());
                        for (String userName : citizenPlayersUserNames) {
                            doctorLecterHandler.sendMessage(userName);
                        }
                        godFatherHandler.sendMessage(doctorLecter.getUserName() + " voted " + server.getHandler(doctorLecter).readMessage());
                    }
                }
                if (simpleMafia != null) {
                    if (simpleMafia.isAlive()) {
                        Handler simpleMafiaHandler = server.getHandler(simpleMafia);
                        simpleMafiaHandler.sendMessage("Get vote");
                        simpleMafiaHandler.sendValue(citizenPlayersUserNames.size());
                        for (String userName : citizenPlayersUserNames) {
                            simpleMafiaHandler.sendMessage(userName);
                        }
                        godFatherHandler.sendMessage(simpleMafia.getUserName() + " voted " + server.getHandler(simpleMafia).readMessage());
                    }
                }
                Player diePlayer = shareData.findPlayerByUserName(godFatherHandler.readMessage());
                if (diePlayer != null) {
                    diePlayer.setInjury(true);

                }
            }
        }
        if (shareData.findPlayerByRoll("Dr.Lecter").isAlive()) {
            DoctorLecter doctorLecter = (DoctorLecter) shareData.findPlayerByRoll("Dr.Lecter");
            if (doctorLecter.isAlive()) {
                Handler doctorLecterHandler = server.getHandler(doctorLecter);
                if (!shareData.findPlayerByRoll("Godfather").isAlive()) {
                    doctorLecterHandler.sendMessage("Kill");
                    SimpleMafia simpleMafia = (SimpleMafia) shareData.findPlayerByRoll("Simple Mafia");
                    doctorLecterHandler.sendValue(shareData.getMafiaPlayersUserNames().size());
                    ArrayList<String> citizenPlayersUserNames = shareData.getCitizenPlayersUserNames();
                    doctorLecterHandler.sendValue(citizenPlayersUserNames.size());
                    for (String userName : citizenPlayersUserNames) {
                        doctorLecterHandler.sendMessage(userName);
                    }
                    if (simpleMafia != null) {
                        if (simpleMafia.isAlive()) {
                            Handler simpleMafiaHandler = server.getHandler(simpleMafia);
                            simpleMafiaHandler.sendMessage("Get vote");
                            simpleMafiaHandler.sendValue(citizenPlayersUserNames.size());
                            for (String userName : citizenPlayersUserNames) {
                                simpleMafiaHandler.sendMessage(userName);
                            }
                        }
                        doctorLecterHandler.sendMessage(server.getHandler(simpleMafia).readMessage());
                    }

                    Player diePlayer = shareData.findPlayerByUserName(doctorLecterHandler.readMessage());
                    if (diePlayer != null) {
                        diePlayer.setInjury(true);
                    }
                }
                doctorLecterHandler.sendMessage("Act");
                ArrayList<String> mafiaUsers = shareData.getMafiaPlayersUserNames();
                doctorLecterHandler.sendValue(mafiaUsers.size());
                for (String userName : mafiaUsers) {
                    if (!userName.equals(doctorLecterHandler.getUserName())) {
                        doctorLecterHandler.sendMessage(userName);
                    }
                }
                Player hillPlayer = shareData.findPlayerByUserName(server.getHandler(doctorLecter).readMessage());
                if (hillPlayer != null) {
                    MafiaPlayer hillPLayer = (MafiaPlayer) hillPlayer;
                    hillPLayer.setHill(true);
                }
            }
        }
        if (shareData.findPlayerByRoll("Simple Mafia") != null) {
            SimpleMafia simpleMafia = (SimpleMafia) shareData.findPlayerByRoll("Simple Mafia");
            if (simpleMafia.isAlive()) {
                Handler simpleMafiaHandler = server.getHandler(simpleMafia);
                if (!shareData.findPlayerByRoll("Godfather").isAlive() && !shareData.findPlayerByRoll("Dr.Lecter").isAlive()) {
                    simpleMafiaHandler.sendMessage("Kill");
                    simpleMafiaHandler.sendValue(shareData.getMafiaPlayersUserNames().size());
                    ArrayList<String> citizenPlayersUserNames = shareData.getCitizenPlayersUserNames();
                    simpleMafiaHandler.sendValue(citizenPlayersUserNames.size());
                    for (String userName : citizenPlayersUserNames) {
                        simpleMafiaHandler.sendMessage(userName);
                    }
                    Player diePlayer = shareData.findPlayerByUserName(simpleMafiaHandler.readMessage());
                    if (diePlayer != null) {
                        diePlayer.setInjury(true);
                    }
                }
            }
        }

        if (shareData.findPlayerByRoll("City Doctor") != null) {
            CityDoctor cityDoctor = (CityDoctor) shareData.findPlayerByRoll("City Doctor");
            if (cityDoctor.isAlive()) {
                server.broadcast("City Doctor wakes up", null);
                server.getHandler(cityDoctor).sendMessage("Act");
                Player hillPlayer = shareData.findPlayerByUserName(server.getHandler(cityDoctor).readMessage());
                if (hillPlayer.isInjury()) {
                    if (hillPlayer instanceof CitizenPlayer) {
                        if (hillPlayer instanceof CityDoctor) {
                            cityDoctor.setStateAbility(false);
                            cityDoctor.hill();
                        }
                        hillPlayer.hill();
                    }
                }
            }
        }

        if (shareData.findPlayerByRoll("Detective") != null) {
            Detective detective = (Detective) shareData.findPlayerByRoll("Detective");
            if (detective.isAlive()) {
                server.broadcast("Detective wakes up", null);
                server.getHandler(detective).sendMessage("Act");
                Player player = shareData.findPlayerByUserName(server.getHandler(detective).readMessage());
                if (player instanceof MafiaPlayer) {
                    if (!(player instanceof GodFather)) {
                        server.getHandler(detective).sendMessage("Approved");
                    } else
                        server.getHandler(detective).sendMessage("Not approved");
                } else
                    server.getHandler(detective).sendMessage("Not approved");
            }
        }
        if (shareData.findPlayerByRoll("Professional") != null) {
            Professional professional = (Professional) shareData.findPlayerByRoll("Professional");
            if (professional.isAlive()) {
                server.broadcast("Professional wakes up", null);
                server.getHandler(professional).sendMessage("Act");
                Player player = shareData.findPlayerByUserName(server.getHandler(professional).readMessage());
                if (player != null) {
                    if (player instanceof CitizenPlayer) {
                        professional.setInjury(true);

                    } else {
                        MafiaPlayer mafiaPlayer = (MafiaPlayer) player;
                        if (mafiaPlayer.isHill()) {
                            mafiaPlayer.setHill(false);
                            if (mafiaPlayer instanceof DoctorLecter) {
                                DoctorLecter doctorLecter = (DoctorLecter) mafiaPlayer;
                                doctorLecter.setStateAbility(false);
                                doctorLecter.setHill(false);
                            }
                        } else {
                            mafiaPlayer.setInjury(true);

                        }
                    }
                }
            }
        }
        if (shareData.findPlayerByRoll("Psychologist") != null) {
            Psychologist psychologist = (Psychologist) shareData.findPlayerByRoll("Psychologist");
            if (psychologist.isAlive()) {
                server.broadcast("Psychologist wakes up", null);
                server.getHandler(psychologist).sendMessage("Act");
                Player player = shareData.findPlayerByUserName(server.getHandler(psychologist).readMessage());
                if (player != null) {
                    player.setSilent(true);
                }
            }
        }
        if (shareData.findPlayerByRoll("Diehard") != null) {
            DieHard dieHard = (DieHard) shareData.findPlayerByRoll("Diehard");
            if (dieHard.isAlive()) {
                server.broadcast("Diehard wakes up", null);
                server.getHandler(dieHard).sendMessage("Act");
                if (dieHard.getInquiryNumber() != 0) {
                    if (server.getHandler(dieHard).readMessage().equals("Yes")) {
                        isInquiry = true;
                        dieHard.setInquiryNumber(dieHard.getInquiryNumber() - 1);
                    }
                }
            }
        }
        Player simpleCitizen = shareData.findPlayerByRoll("Simple Citizen");
        if (simpleCitizen != null) {
            if (simpleCitizen.isAlive()) {
                server.getHandler(simpleCitizen).sendMessage("Act");
            }
        }
        ArrayList<Handler> handlers = new ArrayList<>(server.getPlayerHandler().values());
        for (Handler handler : handlers) {
            if (handler.getPlayer().isInjury()) {
                if (handler.getPlayer() instanceof DieHard) {
                    if (((DieHard) handler.getPlayer()).isExtraLife()) {
                        ((DieHard) handler.getPlayer()).setExtraLife(false);
                        handler.getPlayer().setInjury(false);
                    } else {
                        handler.getPlayer().kill();
                        lastNightDeadUserNames.add(handler.getPlayer().getUserName());
                    }
                } else {
                    handler.getPlayer().kill();
                    lastNightDeadUserNames.add(handler.getPlayer().getUserName());
                }
            }

        }
        server.broadcast("End night", null);
        for (Handler handler : handlers) {
            try {
                handler.sendObject(handler.getPlayer().clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Check end of the game.
     *
     * @return true if game is over.
     */
    public boolean checkEndOfGame() {
        int mafiaNumber = shareData.getMafiaPlayersUserNames().size();
        int citizenNumber = shareData.getCitizenPlayersUserNames().size();
        if ((mafiaNumber >= citizenNumber) || (mafiaNumber == 0)) {
            return true;
        }
        return false;
    }

    /**
     * Sets rolls of the game.
     */
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

    /**
     * Gets player.
     *
     * @param userName the username of the player.
     * @return the roll of the player.
     */
    public static Player getRoll(String userName) {
        int randomValue = new Random().nextInt(rolls.size());
        Player player = new PlayerFactory().getPlayer(rolls.get(randomValue), userName);
        rolls.remove(randomValue);
        return player;
    }

    /**
     * Gets silent player.
     *
     * @return the silent player.
     */
    public Player getSilentPlayer() {
        ArrayList<Player> players = shareData.getPlayers();
        for (Player player : players) {
            if (player.isAlive() && player.isSilent()) {
                return player;
            }
        }
        return null;
    }

    /**
     * Sets number of players.
     *
     * @param numberOfPlayers the number of players
     */
    public static void setNumberOfPlayers(int numberOfPlayers) {
        GameManger.numberOfPlayers = numberOfPlayers;
    }

    /**
     * Send the latest alive users list.
     */
    public void sendTheLatestAliveUsersList() {
        ArrayList<Player> players = shareData.getPlayers();
        ArrayList<Handler> handlers = new ArrayList<>(server.getPlayerHandler().values());
        for (Handler handler : handlers) {
            handler.sendValue(shareData.getNumberOfAlivePlayer());
            for (Player player : players) {
                if (player.isAlive()) {
                    handler.sendMessage(player.getUserName());
                }
            }
        }
    }

    /**
     * Gets number of players.
     *
     * @return the number of players
     */
    public static int getNumberOfPlayers() {
        return numberOfPlayers;
    }
}
