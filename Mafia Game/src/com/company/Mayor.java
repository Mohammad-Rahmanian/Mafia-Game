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
}
