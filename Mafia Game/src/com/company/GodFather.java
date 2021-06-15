package com.company;

/**
 * The godfather player of the game.
 *
 * @author Mohammad Rahmanian.
 * @version 1.0
 */
public class GodFather extends MafiaPlayer {
    /**
     * Instantiates a new God father.
     *
     * @param userName the user name of the godfather player.
     */
    public GodFather(String userName) {
        super(userName, "Godfather");
    }

    /**
     * Executes the َact of the godfather player.
     *
     * @param client the client of the player.
     */
    public void act(Client client) {
        killCitizen(client);
    }
}
