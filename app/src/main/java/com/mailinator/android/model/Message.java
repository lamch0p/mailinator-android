package com.mailinator.android.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LAMDE on 9/29/2014.
 */
public class Message {

    @SerializedName("seconds_ago")
    private long secondsAgo;

    @SerializedName("id")
    private String id;

    @SerializedName("to")
    private String to;

    @SerializedName("time")
    private long time;

    @SerializedName("subject")
    private String subject;

    @SerializedName("fromfull")
    private String fromFull;

    @SerializedName("been_read")
    private boolean beenRead;

    @SerializedName("from")
    private String from;

    @SerializedName("ip")
    private String ip;

    public long getSecondsAgo() {
        return secondsAgo;
    }

    public void setSecondsAgo(long secondsAgo) {
        this.secondsAgo = secondsAgo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFromFull() {
        return fromFull;
    }

    public void setFromFull(String fromFull) {
        this.fromFull = fromFull;
    }

    public boolean beenRead() {
        return beenRead;
    }

    public void setBeenRead(boolean beenRead) {
        this.beenRead = beenRead;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("to: ")
                .append(getTo())
                .append(System.getProperty("line.separator"))
                .append("from: ")
                .append(getFromFull())
                .append(System.getProperty("line.separator"))
                .append("subject: ")
                .append(getSubject())
                .append("id: ")
                .append(getId())
                .append(System.getProperty("line.separator"))
                .append(beenRead() ? "opened" : "unopened")
                .toString();
    }

    @Override
    public boolean equals(Object message) {
        Message otherMessage = (Message) message;
        return getSubject() != null
                && otherMessage.getSubject() != null
                && getSubject().equals(otherMessage.getSubject())
                && getFromFull() != null
                && otherMessage.getFromFull() != null
                && getFromFull().equals(otherMessage.getFromFull());
    }
}
