package com.company;

public abstract class CitizenPlayer extends Player {

    public CitizenPlayer(String userName, String roll) {
        super(userName, roll);
    }

    @Override
    public abstract void act(Client client);
}

