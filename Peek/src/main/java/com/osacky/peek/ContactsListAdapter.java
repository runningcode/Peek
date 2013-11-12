package com.osacky.peek;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.osacky.peek.Models.Contact;

import java.util.List;

public class ContactsListAdapter extends ArrayAdapter<Contact> {
    private LayoutInflater layoutInflater;

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
}
