package com.company;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class DoctorLecter extends MafiaPlayer{
    private boolean saveHimselfAbility;
    public DoctorLecter(String userName) {
        super(userName,"Dr.Lecter");
        saveHimselfAbility = true;
    }
    public void setStateAbility(boolean abilityState){
        this.saveHimselfAbility = abilityState;
    }

    public  boolean getStateAbility() {
        return saveHimselfAbility;
    }
    public void act(ObjectInputStream objectInputStream, PrintWriter writer){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Who do you want to save?");
        try {
            ArrayList<String> mafiaPlayersUserNames = (ArrayList<String>) objectInputStream.readObject();
            if (!saveHimselfAbility) {
                mafiaPlayersUserNames.remove(this.getUserName());
            }
            int counter = 1;
            for (String userName : mafiaPlayersUserNames) {
                System.out.println(counter + ")" + userName);
                counter++;
            }

            int decision;
            while (true){
                try {
                    decision = scanner.nextInt();
                    if (decision < 1 || decision > mafiaPlayersUserNames.size()){
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
            writer.println(mafiaPlayersUserNames.get(decision - 1));






        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
}


