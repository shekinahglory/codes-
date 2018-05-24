package com.shekglory.friends;

public class Messages {



    private String messages, type;

    private long time;

    private boolean seen;





    private String from;

    public Messages(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getTime() {
        return time;
    }

    public Messages(String messages, boolean seen, long time, String type){

        this.messages = messages;
        this.seen = seen;
        this.time = time;
        this.type = type;


    }

    public Messages(){

    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
