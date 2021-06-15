package com.company;


/**
 * The psychologist player of the game.
 *
 * @author Mohammad Rahmanian.
 * @version 1.0
 */
public class Psychologist extends CitizenPlayer {
    /**
     * Instantiates a new Psychologist.
     *
     * @param userName the user name of the Psychologist.
     */
    public Psychologist(String userName) {
        super(userName, "Psychologist");
    }

    /**
     * Executes the َact of the Psychologist player.
     *
     * @param client the client of the player.
     */
    public void act(Client client) {

        System.out.println("Do you want to silence someone?\n1.Yes\n2.No");
        int decision = client.yesOrNoQuestion();
        if (decision == 1) {
            System.out.println("who do you want to silent it?");
            client.sendMessage(client.selectUser(client.getOthersUserNames()));
        } else {
            client.sendMessage(null);
        }
        System.out.println("Your act is over.");
    }

}
