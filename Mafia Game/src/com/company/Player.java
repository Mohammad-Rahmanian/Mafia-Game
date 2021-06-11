package com.company;

import java.io.Serializable;

public class Player implements Serializable,Cloneable {
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
    public Player getInstance(Player player){
        Player p = new  Player(player.userName,player.roll);
        p.setState(player.getState());
        return p;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "Player{" +
                "userName='" + userName + '\'' +
                ", roll='" + roll + '\'' +
                ", isAlive=" + isAlive +
                '}';
    }
}
