package com.company;

import java.util.ArrayList;


/**
 * The mafia player. of the game.
 *
 * @author Mohammad Rahmanian
 * @version 1.0
 */
public abstract class MafiaPlayer extends Player {
    private boolean isHill;

    /**
     * Instantiates a new Mafia player.
     *
     * @param userName the user name of the mafia player.
     * @param roll     the roll of the mafia player.
     */
    public MafiaPlayer(String userName, String roll) {
        super(userName, roll);
        isHill = false;
    }

    /**
     * Executes the َact of the mafia player.
     *
     * @param client the client of the player.
     */
    @Override
    public abstract void act(Client client);

    /**
     * Is hill boolean.
     *
     * @return the boolean.
     */
    public boolean isHill() {
        return isHill;
    }

    /**
     * Sets hill.
     *
     * @param hill the hill.
     */
    public void setHill(boolean hill) {
        isHill = hill;
    }

    /**
     * Kill citizen.
     *
     * @param client the client of the player.
     */
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
        System.out.println("Your act is over.");
    }

    /**
     * Gets vote.
     *
     * @param client the client of the player.
     */
    public void getVote(Client client) {
        ArrayList<String> citizenPlayersUserNames = new ArrayList<>();
        int citizenNumbers = Integer.parseInt(client.readMessage());
        for (int i = 0; i < citizenNumbers; i++) {
            citizenPlayersUserNames.add(client.readMessage());
        }
        System.out.println("Who do you want to die?");
        client.sendMessage(client.selectUser(citizenPlayersUserNames));
        System.out.println("Your voting is over");
    }

}
