package com.company;

import java.io.Serializable;

/**
 * The player of the game.
 *
 * @author Mohammad Rahmanian.
 * @version 1.0
 */
public abstract class Player implements Serializable, Cloneable {

    private String userName;
    private String roll;
    private boolean isAlive;
    private boolean isInjury;
    private boolean isSilent;

    /**
     * Instantiates a new Player.
     *
     * @param userName the user name of the player.
     * @param roll     the roll of the player.
     */
    public Player(String userName, String roll) {
        this.userName = userName;
        this.roll = roll;
        isAlive = true;
        isInjury = false;
        isSilent = false;
    }

    /**
     * Gets user name.
     *
     * @return the user name of the player.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Gets roll.
     *
     * @return the roll of the player.
     */
    public String getRoll() {
        return roll;
    }

    /**
     * Sets user name.
     *
     * @param userName the user name to be set.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


    /**
     * Is alive boolean.
     *
     * @return the state of alive
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * Hill player.
     */
    public void hill() {
        isInjury = false;
    }

    /**
     * Kill player.
     */
    public void kill() {
        isAlive = false;
    }


    /**
     * Is injury boolean.
     *
     * @return the state of injury
     */
    public boolean isInjury() {
        return isInjury;
    }

    /**
     * Sets injury.
     *
     * @param injury the state of injury.
     */
    public void setInjury(boolean injury) {
        isInjury = injury;
    }

    /**
     * Is silent boolean.
     *
     * @return the state of silent
     */
    public boolean isSilent() {
        return isSilent;
    }

    /**
     * Sets silent.
     *
     * @param silent the state of silent
     */
    public void setSilent(boolean silent) {
        isSilent = silent;
    }

    /**
     * Executes the َact of the player.
     *
     * @param client the client of player.
     */
    public abstract void act(Client client);
}
