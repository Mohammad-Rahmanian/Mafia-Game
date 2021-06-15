package com.company;


public class DieHard extends CitizenPlayer {
    private boolean extraLife = true;
    private int inquiryNumber;

    public DieHard(String userName) {
        super(userName, "Diehard");
        inquiryNumber = 2;
    }

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
    }

    @Override
    public void kill() {
        if (extraLife) {
            extraLife = false;
        } else {
            super.kill();
        }
    }

    public int getInquiryNumber() {
        return inquiryNumber;
    }

    public void setInquiryNumber(int inquiryNumber) {
        this.inquiryNumber = inquiryNumber;
    }
}
