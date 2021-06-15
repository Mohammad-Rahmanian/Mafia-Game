package com.company;

import java.util.ArrayList;


/**
 * The doctor Lecter player of the game.
 *
 * @author Mohammad Rahmanian.
 * @version 1.0
 */
public class DoctorLecter extends MafiaPlayer {
    private boolean saveHimselfAbility;

    /**
     * Instantiates a new Doctor lecter.
     *
     * @param userName the user name of the doctor Lecter player.
     */
    public DoctorLecter(String userName) {
        super(userName, "Dr.Lecter");
        saveHimselfAbility = true;
    }

    /**
     * Sets state ability.
     *
     * @param abilityState the ability state.
     */
    public void setStateAbility(boolean abilityState) {
        this.saveHimselfAbility = abilityState;
    }
    /**
     * Executes the َact of the doctor Lecter player.
     *
     * @param client the client of the player.
     */
    @Override
    public void act(Client client) {
        ArrayList<String> mafiaPlayersUserNames = new ArrayList<>();
        int mafiaNumbers = Integer.parseInt(client.readMessage());
        for (int i = 0; i < mafiaNumbers - 1; i++) {
            mafiaPlayersUserNames.add(client.readMessage());
        }
        int decision = 2;
        if (saveHimselfAbility) {
            System.out.println("Do you want to save yourself\n1.Yes\n2.No");
            decision = client.yesOrNoQuestion();
        }
        if (decision == 1) {
            client.sendMessage(client.getUserName());
        } else {
            System.out.println("Who do you want to save?");
            client.sendMessage(client.selectUser(mafiaPlayersUserNames));
        }
        System.out.println("Your act is over.");
    }
}


