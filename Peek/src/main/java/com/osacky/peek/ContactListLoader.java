package com.osacky.peek;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.content.AsyncTaskLoader;

import java.util.HashMap;
import java.util.Map;

public class ContactListLoader extends AsyncTaskLoader<Map<String, String>> {

    private Context context;

    public ContactListLoader(Context context) {
        super(context);
        this.context = context;

    }
    @Override
    public Map<String, String> loadInBackground() {
        Map<String, String> contacts = new HashMap<String, String>();
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (phones == null) {
            return null;
        }
        while (phones.moveToNext())
        {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if (phoneNumber == null || name == null) {
                continue;
            }
            phoneNumber = phoneNumber.replaceAll("\\D", "");
            contacts.put(phoneNumber, name);
        }
        phones.close();
        return contacts;
    }
}
