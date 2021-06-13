package com.company;

import java.io.PrintWriter;
import java.io.Serializable;

public class Player implements Serializable,Cloneable {
    private String userName;
    private String roll;
    private boolean isAlive;
//    private boolean isInjury;
    private boolean isSilent;

    public Player(String userName,String roll){
        this.userName = userName;
        this.roll = roll;
        isAlive = true;
//        isInjury = false;
        isSilent = false;
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

//    public synchronized void setAliveState(boolean isAlive) {
//        this.isAlive = isAlive;
//    }
//
//    public synchronized boolean getAliveState() {
//        return isAlive;
//    }


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

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

//    public boolean isInjury() {
//        return isInjury;
//    }
//
//    public void setInjury(boolean injury) {
//        isInjury = injury;
//    }

    public boolean isSilent() {
        return isSilent;
    }

    public void setSilent(boolean silent) {
        isSilent = silent;
    }
    //    public void act(ShareData shareData, PrintWriter writer){
//
//    }
}
