package com.company;

/**
 * The city detective player of the game.
 *
 * @author Mohammad Rahmanian.
 * @version 1.0
 */
public class Detective extends CitizenPlayer {
    /**
     * Instantiates a new Detective.
     *
     * @param userName the user name of the detective.
     */
    public Detective(String userName) {
        super(userName, "Detective");
    }

    /**
     * Executes the َact of the detective player.
     *
     * @param client the client of the player.
     */
    @Override
    public void act(Client client) {
        System.out.println("Which user do you want to know the role of?");
        client.sendMessage(client.selectUser(client.getOthersUserNames()));
        System.out.println(client.readMessage());
        System.out.println("Your act is over");
    }
}
