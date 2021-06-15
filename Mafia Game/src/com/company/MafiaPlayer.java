package com.company;

import java.util.ArrayList;


public abstract class MafiaPlayer extends Player {
    private boolean isHill;

    public MafiaPlayer(String userName, String roll) {
        super(userName, roll);
        isHill = false;
    }

    @Override
    public abstract void act(Client client);

    public boolean isHill() {
        return isHill;
    }

    public void setHill(boolean hill) {
        isHill = hill;
    }

    public void killCitizen(Client client) {
        ArrayList<String> citizenPlayersUserNames = new ArrayList<>();
        int mafiaNumbers = Integer.parseInt(client.readMessage());
        int citizenNumbers = Integer.parseInt(client.readMessage());
        for (int i = 0; i < citizenNumbers; i++) {
            citizenPlayersUserNames.add(client.readMessage());
        }
        for (int i = 0; i < mafiaNumbers - 1; i++) {
            System.out.println(client.readMessage());
        }
        System.out.println("Who do you want to kill?");
        client.sendMessage(client.selectUser(citizenPlayersUserNames));
    }

    public void getVote(Client client) {
        ArrayList<String> citizenPlayersUserNames = new ArrayList<>();
        int citizenNumbers = Integer.parseInt(client.readMessage());
        for (int i = 0; i < citizenNumbers; i++) {
            citizenPlayersUserNames.add(client.readMessage());
        }
        System.out.println("Who do you want to die?");
        client.sendMessage(client.selectUser(citizenPlayersUserNames));
    }

}
