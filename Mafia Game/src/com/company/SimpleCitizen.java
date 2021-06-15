package com.company;

/**
 * The simple citizen player of the game.
 *
 * @author Mohammad Rahmanian.
 * @version 1.0
 */
public class SimpleCitizen extends CitizenPlayer {
    /**
     * Instantiates a new Simple citizen.
     *
     * @param userName the user name of the simple citizen.
     */
    public SimpleCitizen(String userName) {
        super(userName, "Simple Citizen");
    }

    /**
     * Executes the َact of the simple citizen player.
     *
     * @param client the client of the player.
     */
    @Override
    public void act(Client client) {
    }
}
