package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MafiaPlayer extends Player{
    private boolean isHill;
    public MafiaPlayer(String userName,String roll) {
        super(userName,roll);
        isHill = false;
    }

    public boolean isHill() {
        return isHill;
    }

    public void setHill(boolean hill) {
        isHill = hill;
    }
    public void killCitizen(ObjectInputStream objectInputStream, BufferedReader reader, PrintWriter writer){
        try {
            Scanner scanner = new Scanner(System.in);
            ArrayList<String> mafiaPlayersUserNames = (ArrayList<String>) objectInputStream.readObject();
            ArrayList<String> citizenPlayersUserNames = (ArrayList<String>) objectInputStream.readObject();
            for (int i = 0;i<mafiaPlayersUserNames.size() - 1;i++){
                System.out.println(reader.readLine());
            }
            System.out.println("Who do you want to kill?");
            int counter = 1;
            for (String userName : citizenPlayersUserNames){
                System.out.println(counter + ")" + userName);
                counter++;
            }
            int decision;
            while (true){
                try {
                    decision = scanner.nextInt();
                    if (decision < 1 || decision > citizenPlayersUserNames.size()){
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
            writer.println(citizenPlayersUserNames.get(decision - 1));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void getVote(PrintWriter writer,ObjectInputStream objectInputStream){
        try {
            Scanner scanner = new Scanner(System.in);
            ArrayList<String> citizenPlayersUserNames = (ArrayList<String>) objectInputStream.readObject();
            System.out.println("Who do you want to die?");
            int counter = 1;
            for (String userName : citizenPlayersUserNames){
                System.out.println(counter + ")" + userName);
                counter++;
            }
            int decision;
            while (true){
                try {
                    decision = scanner.nextInt();
                    if (decision < 1 || decision > citizenPlayersUserNames.size()){
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
            writer.println(citizenPlayersUserNames.get(decision - 1));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }




}
