package com.company;

public class PlayerFactory {
    public Player getPlayer(String roll,String userName){
        if (roll == null){
            return null;
        }
        else if (roll.equals("Godfather")){
            return new GodFather(userName);
        }
        else if (roll.equals("Dr.Lecter")){
            return new DoctorLecter(userName);
        }
        else if (roll.equals("Simple Mafia")){
            return new SimpleMafia(userName);
        }
        else if (roll.equals("City Doctor")){
            return new CityDoctor(userName);
        }
        else if (roll.equals("Simple Citizen")){
            return new SimpleCitizen(userName);
        }
        else if (roll.equals("Detective")){
            return new Detective(userName);
        }
        else if (roll.equals("Diehard")){
            return new DieHard(userName);
        }
        else if (roll.equals("Psychologist")){
            return new Psychologist(userName);
        }
        else if (roll.equals("Professional")){
            return new Professional(userName);
        }
        else if (roll.equals("Mayor")){
            return new Mayor(userName);
        }
        return null;
    }
}
