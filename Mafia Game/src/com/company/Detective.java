package com.company;

public class Detective extends CitizenPlayer {
    public Detective(String userName) {
        super(userName, "Detective");
    }

    @Override
    public void act(Client client) {
        System.out.println("Which user do you want to know the role of?");
        client.sendMessage(client.selectUser(client.getOthersUserNames()));
        System.out.println(client.readMessage());
    }
}
