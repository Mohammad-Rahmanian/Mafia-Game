package com.company;

public class Mayor extends CitizenPlayer {
    private boolean cancelVotingAbility;

    public Mayor(String userName) {
        super(userName, "Mayor");
        cancelVotingAbility = true;
    }

    public void setStateAbility(Boolean cancelVotingAbility) {
        this.cancelVotingAbility = cancelVotingAbility;
    }

    public boolean getStateAbility() {
        return cancelVotingAbility;
    }

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
