package com.example.triph.chat_sever;

class Sms {
    private String username;
    private String sms;
    private long timestamp;
    private boolean isMine;

    public boolean isMine() {
        return isMine;
    }

    public String getUsername() {
        return username;
    }

    Sms(String user, String sms, long timestamp, boolean isMine) {
        this.username = user;
        this.sms = sms;
        this.timestamp = timestamp;
        this.isMine = isMine;
    }

    String getSms() {
        return sms;
    }

    long getTimestamp() {
        return timestamp;
    }
}
