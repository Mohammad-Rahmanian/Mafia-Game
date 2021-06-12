package com.company;

import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.Scanner;

public class DieHard extends CitizenPlayer{
    private boolean extraLife = true;
    private  int inquiryNumber;
    public DieHard(String userName) {
        super(userName,"Diehard");
    }
    public void act(PrintWriter writer) {
        if (inquiryNumber != 0) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Do you want to inquire?\n1.Yes\n2.No");
//        System.out.println("Do you want to use your role?\n1.Yes\n2.No");
            int decision;
            while (true) {
                try {
                    decision = scanner.nextInt();
                    if (decision != 1 && decision != 2) {
                        System.out.println("Invalid input");
                        continue;
                    }
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input");
//                e.printStackTrace(System.err);
                }
            }
            if (decision == 1) {
                writer.println("Yes");
            } else writer.println("No");
        }
    }

    public void setExtraLife(boolean extraLife) {
        this.extraLife = extraLife;
    }

    public boolean isExtraLife() {
        return extraLife;
    }

    public int getInquiryNumber() {
        return inquiryNumber;
    }

    public void setInquiryNumber(int inquiryNumber) {
        this.inquiryNumber = inquiryNumber;
    }
}
