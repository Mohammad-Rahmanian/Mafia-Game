package com.company;

import java.io.Serializable;

public class Player implements Serializable {
    private String userName;
    private String roll;

    public Player(String userName,String roll){
        this.userName = userName;
        this.roll = roll;
    }

    public String getUserName() {
        return userName;
    }

    public String getRoll() {
        return roll;
    }
}
