package com.company;

/**
 * The professional player of the game.
 *
 * @author Mohammad Rahmanian.
 * @version 1.0
 */
public class Professional extends CitizenPlayer{
    /**
     * Instantiates a new Professional.
     *
     * @param userName the username of the professional player.
     */
    public Professional(String userName) {
        super(userName,"Professional");
    }

    /**
     * Executes the َact of the professional player.
     *
     * @param client the client of the player.
     */
    public void act(Client client){
        System.out.println("Do you want to kill someone?\n1.Yes\n2.No");
        int decision = client.yesOrNoQuestion() ;

        if (decision == 1){
            System.out.println("Who do you want to kill?");
            client.sendMessage(client.selectUser(client.getOthersUserNames()));
        }
        else client.sendMessage(null);
        System.out.println("Your act is over.");
    }

}
