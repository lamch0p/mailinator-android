package com.mailinator.android.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LAMDE on 9/29/2014.
 */
public class AddressToken {

    @SerializedName("address")
    private String address;

    @SerializedName("inboxname")
    private String inboxName;

    @SerializedName("alternatename")
    private String alternateName;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInboxName() {
        return inboxName;
    }

    public void setInboxName(String inboxName) {
        this.inboxName = inboxName;
    }

    public String getAlternateName() {
        return alternateName;
    }

    public void setAlternateName(String alternateName) {
        this.alternateName = alternateName;
    }
}
