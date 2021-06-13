package com.company;

import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Professional extends CitizenPlayer{
    public Professional(String userName) {
        super(userName,"Professional");
    }
    public void act(ShareData shareData, PrintWriter writer){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to kill someone?");
        int decision;
        while (true) {
            try {
                decision = scanner.nextInt();
                if (decision!=1 && decision!=2) {
                    System.out.println("Invalid input");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input");
//                e.printStackTrace(System.err);
            }
        }
        if (decision == 1){
            System.out.println("Who do you want to kill?");
            shareData.printOthersUserNames(this.getUserName());
            while (true){
                try {
                    decision = scanner.nextInt();
                    if (decision < 1 || decision > shareData.getNumberOfAlivePlayer() - 1){
                        System.out.println("Invalid input");
                        continue;
                    }
                    break;
                }
                catch (InputMismatchException e){
                    System.out.println("Invalid input");
//                e.printStackTrace(System.err);
                }
            }
            writer.println(shareData.getOthersUserNames(this.getUserName()).get(decision - 1));
        }
        else writer.println("Nobody");





    }

}
