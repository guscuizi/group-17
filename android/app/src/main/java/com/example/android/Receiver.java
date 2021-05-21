package com.example.android;


import java.util.ArrayList;
import java.util.UUID;

public class Receiver {

    private String ID;
    private String userName;
    private String passWord;
    private String address;

    private ArrayList<Message> notifications;


    public Receiver(String userName, String passWord, String address){
        UUID uuid = UUID.randomUUID();
        this.ID = uuid.toString();
        this.ID = ID.substring(0, Math.min(ID.length(), 3));

        this.userName=userName;
        this.passWord=passWord;
        this.address=address;
        this.notifications= new ArrayList<Message>();
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return userName;
    }

    public void setName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
    public ArrayList<Message> getNotifications() {
        return notifications;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Receiver{" +
                "ID='" + ID + '\'' +
                ", userName='" + userName + '\'' +
                ", passWord='" + passWord + '\'' +
                '}';
    }
}

