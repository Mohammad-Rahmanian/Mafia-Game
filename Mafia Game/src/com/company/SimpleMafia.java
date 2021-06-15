package com.company;

/**
 * The simple mafia player of the game.
 *
 * @author Mohammad Rahmanian.
 * @version 1.0
 */
public class SimpleMafia extends MafiaPlayer {
    /**
     * Instantiates a new Simple mafia.
     *
     * @param userName the user name of the simple mafia player.
     */
    public SimpleMafia(String userName) {
        super(userName, "Simple Mafia");
    }

    /**
     * Executes the َact of the simple mafia player.
     *
     * @param client the client of the player.
     */
    @Override
    public void act(Client client) {
    }
}
