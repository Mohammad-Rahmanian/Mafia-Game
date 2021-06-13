package com.company;

import java.io.BufferedReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;

public class GodFather extends MafiaPlayer {
    public GodFather(String userName) {
        super(userName, "Godfather");
    }

    public void act(ObjectInputStream objectInputStream, BufferedReader reader, PrintWriter writer) {
        killCitizen(objectInputStream, reader, writer);
    }
}
