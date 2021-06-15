package com.company;

/**
 * The mayor player of the game.
 *
 * @author Mohammad Rahmanian.
 * @version 1.0
 */
public class Mayor extends CitizenPlayer {
    private boolean cancelVotingAbility;

    /**
     * Instantiates a new Mayor.
     *
     * @param userName the user name of the mayor.
     */
    public Mayor(String userName) {
        super(userName, "Mayor");
        cancelVotingAbility = true;
    }

    /**
     * Sets state ability.
     *
     * @param cancelVotingAbility the cancel voting ability.
     */
    public void setStateAbility(Boolean cancelVotingAbility) {
        this.cancelVotingAbility = cancelVotingAbility;
    }

    /**
     * Gets state ability.
     *
     * @return the state ability
     */
    public boolean getStateAbility() {
        return cancelVotingAbility;
    }

    /**
     * Executes the َact of the mayor player.
     *
     * @param client the client of the player.
     */
    public void act(Client client) {
        System.out.println("Do you want to cancel the vote?\n1.Yes\n2.No");
        int decision = client.yesOrNoQuestion();
        if (decision == 1) {
            client.sendMessage("Yes");
        } else if (decision == 2) {
            client.sendMessage("No");
        }


    }
}
