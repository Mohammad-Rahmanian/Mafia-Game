package com.company;

/**
 * The city doctor player of the game.
 *
 * @author Mohammad Rahmanian.
 * @version 1.0
 */
public class CityDoctor extends CitizenPlayer {
    private boolean saveHimselfAbility;

    /**
     * Instantiates a new City doctor.
     *
     * @param userName the user name of the city doctor.
     */
    public CityDoctor(String userName) {
        super(userName, "City Doctor");
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
     * Executes the َact of the city doctor player.
     *
     * @param client the client of the player.
     */
    @Override
    public void act(Client client) {
        int decision = 2;
        if (saveHimselfAbility) {
            System.out.println("Do you want to save yourself\n1.Yes\n2.No");
            decision = client.yesOrNoQuestion();
        }
        if (decision == 1) {
            client.sendMessage(client.getUserName());
        } else {
            System.out.println("Who do you want to save?");
            client.sendMessage(client.selectUser(client.getOthersUserNames()));
        }
        System.out.println("Your act is over.");
    }
}
