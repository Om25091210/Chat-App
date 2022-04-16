package com.example.integratingchat.model;

public class Data_Model {

    private CharSequence text;
    private long timestamp;
    private CharSequence sender;

    public Data_Model(CharSequence text, CharSequence sender) {
        this.text = text;
        this.sender = sender;
        timestamp = System.currentTimeMillis();
    }
    public CharSequence getText() {
        return text;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public CharSequence getSender() {
        return sender;
    }

}
