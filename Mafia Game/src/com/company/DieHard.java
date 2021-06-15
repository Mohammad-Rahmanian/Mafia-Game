package com.company;


/**
 * The diehard player of the game.
 *
 * @author Mohammad Rahmanian.
 * @version 1.0
 */
public class DieHard extends CitizenPlayer {
    private boolean extraLife = true;
    private int inquiryNumber;

    /**
     * Instantiates a new Diehard.
     *
     * @param userName the user name of the diehard.
     */
    public DieHard(String userName) {
        super(userName, "Diehard");
        inquiryNumber = 2;
    }
    /**
     * Executes the َact of the diehard.
     *
     * @param client the client of the diehard.
     */
    public void act(Client client) {
        if (inquiryNumber != 0) {
            System.out.println("Do you want to inquire?\n1.Yes\n2.No");
            int decision = client.yesOrNoQuestion();
            if (decision == 1) {
                client.sendMessage("Yes");
            } else {
                client.sendMessage("No");
            }
        }
        System.out.println("Your act is over.");
    }

    /**
     * Is extra life boolean.
     *
     * @return the boolean.
     */
    public boolean isExtraLife() {
        return extraLife;
    }

    /**
     * Sets extra life.
     *
     * @param extraLife the extra life.
     */
    public void setExtraLife(boolean extraLife) {
        this.extraLife = extraLife;
    }

    /**
     * Gets inquiry number.
     *
     * @return the inquiry number
     */
    public int getInquiryNumber() {
        return inquiryNumber;
    }

    /**
     * Sets inquiry number.
     *
     * @param inquiryNumber the inquiry number.
     */
    public void setInquiryNumber(int inquiryNumber) {
        this.inquiryNumber = inquiryNumber;
    }
}
