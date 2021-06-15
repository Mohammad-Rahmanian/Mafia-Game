package com.company;

/**
 * The citizen player of the game.
 *
 * @author Mohammad Rahmanian.
 * @version 1.0
 */
public abstract class CitizenPlayer extends Player {

    /**
     * Instantiates a new Citizen player.
     *
     * @param userName the user name of the citizen player.
     * @param roll     the roll of the citizen player.
     */
    public CitizenPlayer(String userName, String roll) {
        super(userName, roll);
    }

    /**
     * Executes the َact of the citizen player.
     *
     * @param client the client of the player.
     */
    @Override
    public abstract void act(Client client);
}

