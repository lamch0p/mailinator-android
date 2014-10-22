package com.entrego.android;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.entrego.android.controller.MessageController;
import com.entrego.android.model.ApplicationMessage;
import com.entrego.android.model.Message;
import com.entrego.android.model.Messages;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by LAMDE on 10/21/2014.
 */
public class InboxActivity extends Activity {

    public static final String INBOX_KEY = "INBOX";
    private final long SLEEP_TIME = 20000;
    private final String TAG = getClass().getSimpleName();

    private InboxAdapter inboxAdapter;
    private Date mostRecentMessageDate;
    private List<ApplicationMessage> applicationMessages;
    private List<Message> messages;
    private ListView messageList;
    private String inbox = "charlotte62189";
    private boolean isInterrupted;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.inbox);

        mostRecentMessageDate = new Date(0);

        applicationMessages = new ArrayList<ApplicationMessage>();
        messages = new ArrayList<Message>();

        messageList = (ListView) findViewById(R.id.message_list);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            inbox = bundle.getString(INBOX_KEY);
        }

        inboxAdapter = new InboxAdapter(this, applicationMessages);

        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ApplicationMessage applicationMessage = inboxAdapter.getItem(position);
                Intent messageIntent = new Intent(getApplicationContext(), MessageActivity.class);
                messageIntent.putExtra(MessageActivity.MESSAGE_KEY, new Gson().toJson(applicationMessage));
                startActivity(messageIntent);
            }
        });

        messageList.setAdapter(inboxAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isInterrupted = false;
        checkInbox();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isInterrupted = true;
    }

    private void checkInbox() {
        if (inbox != null && !inbox.isEmpty()) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    while (!isInterrupted) {
                        MessageController.getInstance().checkInbox(inbox, new MessageController.InboxCheckedListener() {
                            @Override
                            public void onInboxChecked(boolean success, Messages messages) {
                                if (success) {
                                    processMessages(messages.list());
                                } else {
                                    Log.e(TAG, "Inbox check failed for inbox: " + inbox);
                                }
                            }
                        });
                        try {
                            Thread.sleep(SLEEP_TIME);
                        } catch (InterruptedException e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }
                    }
                    return null;
                }
            }.execute();
        }
    }

    private void processMessages(List<Message> messages) {
        List<Message> newMessages = new ArrayList<Message>();
        for (Message message : messages) {
            Date messageDate = new Date(message.getTime());
            if (messageDate.after(mostRecentMessageDate)) {
                newMessages.add(message);
                mostRecentMessageDate = messageDate;
            }
        }

        for (Message message : newMessages) {
            openMessage(message);
        }

    }

    private void openMessage(final Message message) {
        if (message != null) {
            String messageId = message.getId();
            if (messageId != null && !messageId.isEmpty()) {
                MessageController.getInstance().openMessage(messageId, new MessageController.MessageOpenedListener() {
                    @Override
                    public void onMessageOpened(String body) {
                        buildApplicationMessage(message, body);
                    }
                });
            }
        }
    }

    private void buildApplicationMessage(Message message, String body) {
        if (message != null && body != null && !body.isEmpty()) {
            getApplicationMessages().add(new ApplicationMessage(message, body));
            inboxAdapter.notifyDataSetChanged();
        }
    }

    public List<ApplicationMessage> getApplicationMessages() {
        return applicationMessages;
    }
}
