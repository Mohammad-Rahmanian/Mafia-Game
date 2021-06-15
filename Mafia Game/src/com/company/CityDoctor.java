package com.company;

public class CityDoctor extends CitizenPlayer {
    private boolean saveHimselfAbility;

    public CityDoctor(String userName) {
        super(userName, "City Doctor");
        saveHimselfAbility = true;
    }

    public void setStateAbility(boolean abilityState) {
        this.saveHimselfAbility = abilityState;
    }

    @Override
    public void act(Client client) {
        int decision = 2;
        if (saveHimselfAbility) {
            System.out.println("Do you want to save yourself\n1.Yes\n2.NO");
            decision = client.yesOrNoQuestion();
        }
        if (decision == 1) {
            client.sendMessage(client.getUserName());
        } else {
            System.out.println("Who do you want to save?");
            client.sendMessage(client.selectUser(client.getOthersUserNames()));
        }

    }
}
