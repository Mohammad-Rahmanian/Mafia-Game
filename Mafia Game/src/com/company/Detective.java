package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Detective extends CitizenPlayer{
    public Detective(String userName) {
        super(userName,"Detective");
    }

//    @Override
    public void act(ShareData shareData, PrintWriter writer, BufferedReader reader) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Whose role do you want to understand?");
        shareData.printOthersUserNames(this.getUserName());
        int decision;
        while (true) {
            try {
                decision = scanner.nextInt();
                if (decision < 1 || decision > shareData.getNumberOfAlivePlayer() - 1) {
                    System.out.println("Invalid input");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input");
//                e.printStackTrace(System.err);
            }
        }
        writer.println(shareData.getOthersUserNames(this.getUserName()).get(decision - 1));
        try {
            System.out.println(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
