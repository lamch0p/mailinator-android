package com.entrego.android.service;

import com.entrego.android.model.AddressToken;
import com.entrego.android.model.Messages;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by LAMDE on 9/29/2014.
 */
public interface MessageService {

    @GET("/grab")
    public void checkInboxInitial(@Query("inbox") String inbox, @Query("address") String address, @Query("time") String time, Callback<Messages> messagesCallback);

    @GET("/grab")
    public void checkInboxSubsequent(@Query("inbox") String inbox, @Query("address") String address, @Query("time") String time, @Query("after") String after, Callback<Messages> messagesCallback);

    @GET("/settttt")
    public void getAddressToken(@Query("box") String box, @Query("time") String time, Callback<AddressToken> addressTokenCallback);

}
