package com.shekglory.friends;

public class Conv {


    public boolean seen;
    public long timestamp;
    public String number;

    public Conv(){

    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Conv(boolean seen, long timestamp, String number) {
        this.seen = seen;
        this.timestamp = timestamp;
        this.number = number;
    }
}
