package com.company;

import java.util.*;

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


    public void startGame() {

        introductionNight();
        day();
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
        voting();
        nightGame();
        while (!checkEndOfGame()) {
            day();
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
            voting();
            nightGame();
        }


    }

    public void day() {
        server.broadcast("Start day", null);
        ArrayList<Handler> handlers = new ArrayList<>(server.getPlayerHandler().values());
        for (Handler handler : handlers) {
            if (lastNightDeadUserNames.contains(handler.getUserName())) {
                handler.exitGame();
            }
        }
        sendTheLatestAliveUsersList();
        for (String userName : lastNightDeadUserNames) {
            server.broadcast(userName + " died last night", null);
        }
        lastNightDeadUserNames.clear();
        if (getSilentPlayer() != null) {
            server.broadcast(getSilentPlayer().getUserName() + " is silent today", null);
        }
        if (isInquiry) {
            ArrayList<String> diedRolls = shareData.getDiedRolls();
            server.broadcast("DieHard has inquired", null);
            for (String roll : diedRolls) {
                server.broadcast(roll + " is dead", null);
            }
            isInquiry = false;
        }
//        server.broadcast("Want to see the old message?\n1.Yes\n2.No", null);
//        Scanner fileScanner = new Scanner("d:\\MafiaGame.txt");
//        for (Handler handler : handlers) {
//            if (handler.getPlayer().isAlive()) {
//                if (Integer.parseInt(handler.readMessage()) == 1) {
//                    while (fileScanner.hasNextLine()) {
//                        handler.sendMessage(fileScanner.nextLine());
//                    }
//                }
//            }
//        }


    }


    public boolean checkEndOfGame() {
        ArrayList<Player> players = shareData.getPlayers();
        int mafiaNumber = 0;
        int citizenNumber = 0;
        for (Player player : players) {
            if (player instanceof MafiaPlayer && player.isAlive()) {
                mafiaNumber++;
            } else if (player instanceof CitizenPlayer) {
                citizenNumber++;
            }
        }
        if (mafiaNumber >= citizenNumber || mafiaNumber == 0) {
            return true;
        }
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
        sendTheLatestAliveUsersList();
        for (Handler handler : handlers) {
            if (handler.getPlayer().isAlive()) {
                String vote = handler.readMessage();

                if (!vote.equals("null")) {
                    server.broadcast(handler.getUserName() + " voted:" + vote, handler);
                    if (userNameVotes.get(vote) != null) {
                        userNameVotes.put(vote, userNameVotes.get(vote) + 1);
                    } else {
                        userNameVotes.put(vote, 0);
                    }
                } else
                    server.broadcast(handler.getUserName() + " did not vote for anyone", handler);
            }
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
                    diePlayer.kill();
                }
            }
        }
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
            server.broadcast("Nobody die", null);
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

        if (shareData.findPlayerByRoll("Godfather") != null) {
            GodFather godFather = (GodFather) shareData.findPlayerByRoll("Godfather");
            if (godFather.isAlive()) {
                DoctorLecter doctorLecter = (DoctorLecter) shareData.findPlayerByRoll("Dr.Lecter");
                SimpleMafia simpleMafia = (SimpleMafia) shareData.findPlayerByRoll("Simple Mafia");
                Handler godFatherHandler = server.getHandler(godFather);
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
                        godFatherHandler.sendMessage(doctorLecter.getUserName() + " voted:" + server.getHandler(doctorLecter).readMessage());
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
                        godFatherHandler.sendMessage(simpleMafia.getUserName() + " voted:" + server.getHandler(simpleMafia).readMessage());
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
                    if (simpleMafia != null){
                        if (simpleMafia.isAlive()){
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
            server.getHandler(simpleCitizen).sendMessage("Act");
        }
        server.broadcast("End night", null);
        ArrayList<Handler> handlers = new ArrayList<>(server.getPlayerHandler().values());
        for (Handler handler : handlers) {
            if (handler.getPlayer().isInjury()) {
                handler.getPlayer().kill();
                lastNightDeadUserNames.add(handler.getPlayer().getUserName());
            }
        }
        for (Handler handler : handlers) {
            try {
                handler.sendObject(handler.getPlayer().clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
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
}
