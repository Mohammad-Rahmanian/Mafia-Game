package com.company;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Mayor extends CitizenPlayer {
   private boolean cancelVotingAbility;

    public Mayor(String userName) {
        super(userName, "Mayor");
        cancelVotingAbility = true;
    }

    public void setStateAbility(Boolean cancelVotingAbility) {
        this.cancelVotingAbility = cancelVotingAbility;
    }

    public boolean getStateAbility() {
        return cancelVotingAbility;
    }
    public void act(Client client){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to cancel the vote?\n1.Yes\n2.No");
        int decision;
        while (true) {
            try {
                decision = scanner.nextInt();
                if (decision != 1 && decision != 2) {
                    System.out.println("Enter 1 or 2");
                }
                break;
            } catch (InputMismatchException e) {
                System.err.println("Invalid input");
                scanner.nextLine();
            }

        }
        if (decision == 1) {
            client.sendMessage("Yes");
        } else if (decision == 2) {
            client.sendMessage("No");
        }









    }
}
