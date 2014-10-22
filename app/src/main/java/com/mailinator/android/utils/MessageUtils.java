package com.mailinator.android.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mailinator.android.model.Message;
import com.mailinator.android.model.Messages;

/**
 * Created by LAMDE on 9/30/2014.
 */
public class MessageUtils {

    private static final String TAG = "MessageUtils";

    public static void sendMessage(Context context, String toEmail, String subject, String body) {
        sendGmail(context, toEmail, subject, body);
    }

    public static String getMessageIdOfTheMostRecentApplicationMessage(Messages messages) {
        String id = null;
        if (messages != null) {
            long secondsAgo = Long.MAX_VALUE;
            for (Message message : messages.list()) {
                long messageSecondsAgo = message.getSecondsAgo();
                if (messageSecondsAgo < secondsAgo) {
                    secondsAgo = messageSecondsAgo;
                    id = message.getId();
                }
            }
        }
        return id;
    }

    private static void sendGmail(Context context, String toEmail, String subject, String body) {
        Intent gmailIntent = new Intent();
        gmailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        gmailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{toEmail});
        gmailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        gmailIntent.putExtra(Intent.EXTRA_TEXT, body);
        try {
            context.startActivity(gmailIntent);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }
}
