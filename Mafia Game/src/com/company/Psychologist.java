package com.company;


public class Psychologist extends CitizenPlayer {
    public Psychologist(String userName) {
        super(userName, "Psychologist");
    }

    public void act(Client client) {

        System.out.println("Do you want to use your role?\n1.Yes\n2.No");
        int decision = client.yesOrNoQuestion();
        if (decision == 1) {
            System.out.println("who do you want to silent it?");
            client.sendMessage(client.selectUser(client.getOthersUserNames()));
        } else{
            client.sendMessage(null);
        }
    }

}
