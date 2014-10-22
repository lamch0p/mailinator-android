package com.mailinator.android.controller;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.mailinator.android.model.AddressToken;
import com.mailinator.android.model.Message;
import com.mailinator.android.model.Messages;
import com.mailinator.android.service.MessageService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.io.IOException;
import java.util.Date;

/**
 * Created by LAMDE on 9/29/2014.
 */
public class MessageController {

    private final String TAG = getClass().getSimpleName();

    private static MessageController instance;

    private MessageService messageService;

    private final String ENDPOINT = "https://www.mailinator.com";
    private String inbox;
    private Messages messages;
    private String addressToken;

    private MessageController() {
        initialize();
    }

    private void initialize() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        messageService = restAdapter.create(MessageService.class);

        messages = new Messages();
    }

    public static MessageController getInstance() {
        if (instance == null) {
            instance = new MessageController();
        }
        return instance;
    }

    public void checkInbox(String inbox, final InboxCheckedListener inboxCheckedListener) {
        //If the message service exists
        if (messageService != null) {
            //And the inbox param has been set
            if (inbox != null) {
                //If the param is equal to our current inbox, simply call refresh
                if (this.inbox != null && this.inbox.equals(inbox)) {
                    refreshInbox(inboxCheckedListener);
                    //Otherwise, set the current inbox to the new inbox.
                } else {
                    this.inbox = inbox;
                    //This is our first request for this inbox, so we need a token
                    updateAddressToken(inboxCheckedListener);
                }
            } else {
                Log.e(TAG, "Cannot check inbox 'null'.");
            }
        }
    }

    public void openMessage(String messageId, final MessageOpenedListener messageOpenedListener) {
        if (messageService != null) {
            if (messageId != null) {
                Log.d(TAG, "Opening message with id: " + messageId);
                OpenMessageAsyncTask openMessageAsyncTask = new OpenMessageAsyncTask().setMessageOpenedListener(new MessageOpenedListener() {
                    @Override
                    public void onMessageOpened(String message) {
                        Log.d(TAG, "Message opened: " + message);
                        if (messageOpenedListener != null) {
                            messageOpenedListener.onMessageOpened(parseOpenedMessaged(message));
                        }
                    }
                });

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB_MR1) {
                    openMessageAsyncTask.execute(messageId);
                } else {
                    openMessageAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, messageId);
                }

//                messageService.openMessage(messageId, getTimestamp(), new Callback<String>() {
//                    @Override
//                    public void success(String message, Response response) {
//                        Log.d(TAG, "Successfully opened message.");
//                        parseOpenedMessaged(message);
//                    }
//
//                    @Override
//                    public void failure(RetrofitError error) {
//                        Log.e(TAG, "Failed to open message.");
//                    }
//                });
            }
        }
    }

    private String getTimestamp() {
        return String.valueOf(new Date().getTime());
    }

    private class OpenMessageAsyncTask extends AsyncTask<String, Void, String> {

        private MessageOpenedListener messageOpenedListener;

        public OpenMessageAsyncTask setMessageOpenedListener(MessageOpenedListener messageOpenedListener) {
            this.messageOpenedListener = messageOpenedListener;
            return this;
        }

        @Override
        protected String doInBackground(String... strings) {
            String messageId = strings[0];
            if (messageId != null) {
                HttpClient httpClient = new DefaultHttpClient();
                String openMessageEndpoint = new StringBuilder()
                        .append(ENDPOINT)
                        .append("/rendermail.jsp")
                        .append("?")
                        .append("msgid=")
                        .append(messageId)
                        .append("&")
                        .append("time=")
                        .append(getTimestamp())
                        .toString();
                HttpGet httpGet = new HttpGet(openMessageEndpoint);
//                httpGet.addHeader("Content-Type", "text/html");
//                httpGet.setParams(new BasicHttpParams()
//                        .setParameter("msgid", messageId)
//                        .setParameter("time", time));
                try {
                    return parseHttpResponse(httpClient.execute(httpGet));
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
            return null;
        }

        private String parseHttpResponse(HttpResponse httpResponse) {
            if (httpResponse != null) {
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                Log.d(TAG, "Open Message status code: " + statusCode);
                if (statusCode == HttpStatus.SC_OK) {
                    HttpEntity httpEntity = httpResponse.getEntity();
                    if (httpEntity != null) {
                        try {
                            return EntityUtils.toString(httpEntity);
                        } catch (IOException e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null && messageOpenedListener != null) {
                Log.d(TAG, "Notifying message opened listener.");
                messageOpenedListener.onMessageOpened(response);
            }
        }
    }

    public interface InboxCheckedListener {
        void onInboxChecked(boolean success, Messages messages);
    }

    public interface MessageOpenedListener {
        void onMessageOpened(String message);
    }

    private String parseOpenedMessaged(String message) {
        if (message != null) {
            Log.d(TAG, "Parsing opened message.");
            Document messageDocument = Jsoup.parse(message);
            Elements mailviewElements = messageDocument.getElementsByClass("mailview");
            if (mailviewElements != null) {
                Log.d(TAG, "Parsed mail message.");
                return mailviewElements.text();
            }
        }
        return null;
    }

    private void checkInboxInitial(final InboxCheckedListener inboxCheckedListener) {
        Log.d(TAG, "Checking inbox for the first time. Inbox name: " + inbox);
        messageService.checkInboxInitial(inbox, addressToken, getTimestamp(), new Callback<Messages>() {
            @Override
            public void success(Messages messages, Response response) {
                Log.d(TAG, "Successfully checked inbox for the first time.");
//                String responseString = parseRetrofitResponse(response);
//                Log.d(TAG, responseString);
                updateMessageList(messages);
                if (inboxCheckedListener != null) {
                    inboxCheckedListener.onInboxChecked(true, new Messages(messages));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to check the inbox for the first time.");
                if (inboxCheckedListener != null) {
                    inboxCheckedListener.onInboxChecked(false, null);
                }
            }
        });
    }

    private void refreshInbox(final InboxCheckedListener inboxCheckedListener) {
        Log.d(TAG, "Refreshing inbox: " + inbox);
        messageService.checkInboxSubsequent(inbox, getAddressToken(), getTimestamp(), getAfterMessageIdParameter(), new Callback<Messages>() {
            @Override
            public void success(Messages messages, Response response) {
                Log.d(TAG, "Successfully checked inbox for a subsequent time.");
                updateMessageList(messages);
                if (inboxCheckedListener != null) {
                    inboxCheckedListener.onInboxChecked(true, new Messages(messages));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to check the inbox for a subsequent time.");
                if (inboxCheckedListener != null) {
                    inboxCheckedListener.onInboxChecked(false, null);
                }
            }
        });
    }

    private void updateAddressToken(final InboxCheckedListener inboxCheckedListener) {
        if (messageService != null) {
            Log.d(TAG, "Retrieving address token information.");
            messageService.getAddressToken(inbox, getTimestamp(), new Callback<AddressToken>() {
                @Override
                public void success(AddressToken addressToken, Response response) {
                    Log.d(TAG, "Successfully retrieved address token info.");
                    setAddressToken(addressToken.getAddress());
                    checkInboxInitial(inboxCheckedListener);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "Failed to retrieve address token info.");
                }
            });
        }
    }

    private String getAddressToken() {
        return addressToken;
    }

    private void setAddressToken(String addressToken) {
        this.addressToken = addressToken;
    }

    private void updateMessageList(Messages messages) {
        if (messages != null) {
            for (Message message : messages.list()) {
                getMessages().list().add(message);
            }
        }
    }

    private Messages getMessages() {
        return messages;
    }

    private String getAfterMessageIdParameter() {
        String messageId = null;
        if (getMessages() != null) {
            long min = Long.MAX_VALUE;
            for (Message message : getMessages().list()) {
                long tempMin = message.getSecondsAgo();
                if (tempMin < min) {
                    messageId = message.getId();
                }
            }
        }
        return messageId;
    }
}
