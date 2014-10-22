package com.mailinator.android.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LAMDE on 9/30/2014.
 */
public class ApplicationMessage {

    @SerializedName("message")
    private Message message;

    @SerializedName("body")
    private String body;

    public ApplicationMessage(Message message, String body) {
        this.message = message;
        this.body = body;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
