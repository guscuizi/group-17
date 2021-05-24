package com.example.android;

import java.util.UUID;

public class Message {
    private String ID;
    private String content;
    private String senderID;
    private String sender;
    private String receiver;

    public Message(String content, String receiverID, String sender, String receiver) {
        UUID uuid = UUID.randomUUID();
        this.ID = uuid.toString();
        this.ID = ID.substring(0, Math.min(ID.length(), 3));

        this.content = content;
        this.senderID=senderID;
        this.sender=sender;
        this.receiver=receiver;
    }

    public String getID() {
        return ID;
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }


    public void setContent(String content) {
        this.content = content;
    }



    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    @Override
    public String toString() {
        return "Message{" +
                "ID='" + ID + '\'' +
                ", content='" + content + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                '}';
    }
}
