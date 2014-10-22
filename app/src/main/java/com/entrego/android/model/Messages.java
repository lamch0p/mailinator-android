package com.entrego.android.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LAMDE on 9/29/2014.
 */
public class Messages {

    @SerializedName("maildir")
    private List<Message> messages;

    public Messages() {
        messages = new ArrayList<Message>();
    }

    public Messages(Messages messages) {
        this.messages = messages.list();
    }

    public List<Message> list() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
