package com.company;

public class GodFather extends MafiaPlayer {
    public GodFather(String userName) {
        super(userName, "Godfather");
    }

    public void act(Client client) {
        killCitizen(client);
    }
}
