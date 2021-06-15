package com.company;

import java.util.ArrayList;


public class DoctorLecter extends MafiaPlayer {
    private boolean saveHimselfAbility;

    public DoctorLecter(String userName) {
        super(userName, "Dr.Lecter");
        saveHimselfAbility = true;
    }

    public void setStateAbility(boolean abilityState) {
        this.saveHimselfAbility = abilityState;
    }

    public boolean getStateAbility() {
        return saveHimselfAbility;
    }

    @Override
    public void act(Client client) {
        ArrayList<String> mafiaPlayersUserNames = new ArrayList<>();
        int mafiaNumbers = Integer.parseInt(client.readMessage());
        for (int i = 0; i < mafiaNumbers - 1; i++) {
            mafiaPlayersUserNames.add(client.readMessage());
        }
        int decision = 2;
        if (saveHimselfAbility) {
            System.out.println("Do you want to save yourself\n1.Yes\n2.NO");
            decision = client.yesOrNoQuestion();
        }
        if (decision == 1) {
            client.sendMessage(client.getUserName());
        } else {
            System.out.println("Who do you want to save?");
            client.sendMessage(client.selectUser(mafiaPlayersUserNames));
        }

    }
}


