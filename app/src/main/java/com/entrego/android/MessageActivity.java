package com.entrego.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.entrego.android.model.ApplicationMessage;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LAMDE on 10/22/2014.
 */
public class MessageActivity extends Activity {

    public static final String MESSAGE_KEY = "MESSAGE";
    private final String TAG = getClass().getSimpleName();
    private final String DATE_FORMAT_PATTERN = "EEE MMM dd yyyy HH:mm:ss z";
    private DateFormat dateFormat;
    private ApplicationMessage message;

    private TextView messageTo;
    private TextView messageFrom;
    private TextView messageSubject;
    private TextView messageTime;
    private TextView messageBody;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            message = new Gson().fromJson(bundle.getString(MESSAGE_KEY), ApplicationMessage.class);

            dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);

            initializeView();
            updateView();
        } else {
            Log.d(TAG, "Bundle was null. Unable to render message.");
        }
    }

    private void initializeView() {
        setContentView(R.layout.message);
        messageTo = (TextView) findViewById(R.id.message_to);
        messageFrom = (TextView) findViewById(R.id.message_from);
        messageSubject = (TextView) findViewById(R.id.message_subject);
        messageTime = (TextView) findViewById(R.id.message_time);
        messageBody = (TextView) findViewById(R.id.message_body);
    }


    private ApplicationMessage getMessage() {
        return message;
    }

    private void updateView() {
        messageTo.setText(getMessage().getMessage().getTo());
        messageFrom.setText(getMessage().getMessage().getFrom());
        messageSubject.setText(getMessage().getMessage().getSubject());
        Date time = new Date(getMessage().getMessage().getTime());
        messageTime.setText(dateFormat.format(time));
        messageBody.setText(getMessage().getBody());
    }
}
