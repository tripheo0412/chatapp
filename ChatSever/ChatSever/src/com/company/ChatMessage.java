package com.company;

public class ChatMessage {
    String message;
    long timestamp;
    User ownuser;
    ChatMessage(String input, User user)
    {
        this.ownuser= user;
        this.message = input;
        this.timestamp = System.currentTimeMillis();
    }
    public String messageOutput(){
        return message;
    }
    public User userOwnBy(){return ownuser;}
    public long timeStampOutput() {return timestamp;}
}
