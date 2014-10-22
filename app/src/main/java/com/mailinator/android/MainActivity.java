package com.mailinator.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    private final String LAST_CHECKED_INBOX_NAME_KEY = "LastCheckedInboxNameKey";
    private final String TAG = getClass().getSimpleName();

    private EditText inboxNameEditText;
    private Button checkItButton;
    private TextView lastCheckedInboxNameTextView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        initializeView();
    }

    private void initializeView() {
        setContentView(R.layout.activity_main);
        inboxNameEditText = (EditText) findViewById(R.id.inbox_name);
        checkItButton = (Button) findViewById(R.id.check_it);
        checkItButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inbox = inboxNameEditText.getText().toString();
                if (inbox != null) {
                    sharedPreferences.edit().putString(LAST_CHECKED_INBOX_NAME_KEY, inbox).apply();
                    Intent inboxIntent = new Intent(getApplicationContext(), InboxActivity.class);
                    inboxIntent.putExtra(InboxActivity.INBOX_KEY, inbox);
                    startActivity(inboxIntent);
                }
            }
        });
        lastCheckedInboxNameTextView = (TextView) findViewById(R.id.last_checked_inbox_name);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshLastCheckedInboxName();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshLastCheckedInboxName(){
        String lastCheckedInboxName = new StringBuilder()
                .append("'")
                .append(sharedPreferences.getString(LAST_CHECKED_INBOX_NAME_KEY, ""))
                .append("@mailinator.com")
                .append("'")
                .toString();

        lastCheckedInboxNameTextView.setText(lastCheckedInboxName);
    }
}
