package com.osacky.peek;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.content.AsyncTaskLoader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.osacky.peek.Models.Contact;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ContactListLoader extends AsyncTaskLoader<Map<String, Contact>> {

    private Context context;
    private SharedPreferences sharedPreferences;

    public ContactListLoader(Context context) {
        super(context);
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    @Override
    public Map<String, Contact> loadInBackground() {
        Gson gson = new Gson();
        if (sharedPreferences.contains("contactsMap")) {
            Type type = new TypeToken<Map<String, Contact>>(){}.getType();
            return gson.fromJson(sharedPreferences.getString("contactsMap", ""), type);
        }
        Map<String, Contact> contacts = new HashMap<String, Contact>();
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (phones == null) {
            return null;
        }
        while (phones.moveToNext())
        {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String photoURI = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            if (phoneNumber == null || name == null) {
                continue;
            }
            phoneNumber = phoneNumber.replaceAll("\\D", "");
            Contact contact = new Contact(name, phoneNumber);
            if (photoURI != null) {
                contact.setPhotoURI(photoURI);
            }
            contacts.put(phoneNumber, contact);
        }
        phones.close();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("contactsMap", gson.toJson(contacts));
        editor.commit();

        return contacts;
    }
}
