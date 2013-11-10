package com.osacky.peek;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.osacky.peek.Models.Contact;

public class ContactsListAdapter extends ArrayAdapter<Contact> {
    private LayoutInflater layoutInflater;

    public ContactsListAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        Contact contact = getItem(position);

        if (contact != null) {
            TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
            textView.setText(contact.getName());
        }
        return convertView;
    }
}
