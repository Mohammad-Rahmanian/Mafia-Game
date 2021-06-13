package com.company;

import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.Scanner;

public class CityDoctor extends CitizenPlayer{
private boolean saveHimselfAbility;

    public CityDoctor(String userName) {
        super(userName,"City Doctor");
        saveHimselfAbility = true;
    }
    public void setStateAbility(boolean abilityState){
        this.saveHimselfAbility = abilityState;
    }

    public  boolean getStateAbility() {
        return saveHimselfAbility;
    }

//    @Override
    public void act(ShareData shareData, PrintWriter writer) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Who do you want to save?");
        if (saveHimselfAbility) {
            shareData.printOthersUserNames(null);
        }
        else
            shareData.printOthersUserNames(this.getUserName());
        int decision;
        while (true){
            try {
                decision = scanner.nextInt();
                if (decision < 1 || decision > shareData.getNumberOfAlivePlayer()){
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
        writer.println(shareData.getOthersUserNames(null).get(decision - 1));
    }
}
