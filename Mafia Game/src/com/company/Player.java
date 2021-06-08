package com.company;

import java.io.Serializable;

public class Player implements Serializable {
    private String userName;
    private String roll;
    private boolean isAlive;

    public Player(String userName,String roll){
        this.userName = userName;
        this.roll = roll;
        isAlive = true;
    }

    public String getUserName() {
        return userName;
    }

    public String getRoll() {
        return roll;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public synchronized void setState(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public synchronized boolean getState() {
        return isAlive;
    }
}
