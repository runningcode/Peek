package com.osacky.peek;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.content.AsyncTaskLoader;

import com.osacky.peek.Models.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactListLoader extends AsyncTaskLoader<List<Contact>> {

    private Context context;

    public ContactListLoader(Context context) {
        super(context);
        this.context = context;

    }
    @Override
    public List<Contact> loadInBackground() {
        List<Contact> contacts = new ArrayList<Contact>();
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones != null && phones.moveToNext())
        {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Contact contact = new Contact(name);
            contact.addPhone(phoneNumber);
        }
        phones.close();
        return contacts;
    }
}
