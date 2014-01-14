package com.osacky.peek;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.osacky.peek.Animation.AnimationFactory;
import com.osacky.peek.Models.Contact;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ContactsListAdapter extends ArrayAdapter<Contact> {

    private LayoutInflater layoutInflater;
    private HashMap<String, Long> sentTimes = new HashMap<String, Long>();

    public ContactsListAdapter(Context context) {
        super(context, R.layout.user_item);
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.user_item, parent, false);
        }

        Contact contact = getItem(position);

        if (contact != null) {
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
            TextView textView = (TextView) convertView.findViewById(R.id.name);
            if (contact.getPhotoURI() != null) {
                imageView.setImageURI(Uri.parse(contact.getPhotoURI()));
            }
            textView.setText(contact.getName());
        }
        return convertView;
    }

    public void setData(List<Contact> contacts) {
        if (contacts != null) {
            clear();
            for (Contact contact : contacts) {
                add(contact);
            }
        }
    }

    public void addSentTime(String phone, View v) {
        if (sentTimes.containsKey(phone)) {
            if (sentTimes.get(phone) - System.currentTimeMillis() < TimeUnit.MINUTES.toMillis(10)) {
                return;
            }
        }
        sentTimes.put(phone, System.currentTimeMillis());

        ViewFlipper viewFlipper = (ViewFlipper) v.findViewById(R.id.viewFlipper);
        AnimationFactory.flipTransition(viewFlipper, AnimationFactory.FlipDirection.RIGHT_LEFT);

        ParseQuery<ParseInstallation> parseInstallationQuery = ParseInstallation.getQuery();
        parseInstallationQuery.whereEqualTo("username", phone);

        JSONObject data = new JSONObject();
        try {
            data.put("action", "com.osacky.peek.create");
            data.put("username", Utils.getUserPhoneNumber(getContext()));
            data.put("time", System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ParsePush push = new ParsePush();
        push.setQuery(parseInstallationQuery);
        push.setData(data);
        push.sendInBackground();

        return;

    }
}
