package com.company;

public class Professional extends CitizenPlayer{
    public Professional(String userName) {
        super(userName,"Professional");
    }
    public void act(Client client){
        System.out.println("Do you want to kill someone?\n1.Yes\n2.No");
        int decision = client.yesOrNoQuestion() ;

        if (decision == 1){
            System.out.println("Who do you want to kill?");
            client.sendMessage(client.selectUser(client.getOthersUserNames()));
        }
        else client.sendMessage(null);
    }

}
