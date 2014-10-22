package com.entrego.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.entrego.android.model.ApplicationMessage;

import java.util.List;

/**
 * Created by LAMDE on 10/21/2014.
 */
public class InboxAdapter extends ArrayAdapter<ApplicationMessage> {
    private final Context context;
    private final List<ApplicationMessage> messages;

    public InboxAdapter(Context context, List<ApplicationMessage> messages) {
        super(context, R.layout.message_list_item, messages);
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getCount() {
        return messages != null ? messages.size() : 0;
    }

    @Override
    public ApplicationMessage getItem(int location) {
        return messages != null ? messages.get(location) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ApplicationMessage message = getItem(position);
        View messageView = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            messageView = inflater.inflate(R.layout.message_list_item, parent, false);
        }

        TextView messageSender = (TextView) messageView.findViewById(R.id.message_sender);
        messageSender.setText(message.getMessage().getFrom());
        TextView messageSubject = (TextView) messageView.findViewById(R.id.message_subject);
        messageSubject.setText(message.getMessage().getSubject());
        ImageView messageSenderIcon = (ImageView) messageView.findViewById(R.id.message_sender_icon);
        messageSenderIcon.setBackgroundColor(context.getResources().getColor(android.R.color.black));
        String firstLetter = message.getMessage().getFrom().substring(0, 1);
        TextView messageSenderFirstLetter = (TextView) messageView.findViewById(R.id.message_sender_first_letter);
        messageSenderFirstLetter.setText(firstLetter);

//        boolean beenRead = message.getMessage().beenRead();
//        if(!beenRead){
//            messageSubject.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
//            messageSubject.setTypeface(null, Typeface.BOLD);
//        }

        return messageView;
    }
}
