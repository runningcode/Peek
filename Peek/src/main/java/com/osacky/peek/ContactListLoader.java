package com.osacky.peek;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.osacky.peek.Models.Contact;
import com.osacky.peek.Models.Person;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ContactListLoader extends AsyncTaskLoader<List<Contact>> {

    private Context context;
    private SharedPreferences sharedPreferences;

    public ContactListLoader(Context context) {
        super(context);
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    @Override
    public List<Contact> loadInBackground() {
        Gson gson = new Gson();
        if (sharedPreferences.contains("time")) {
            if ((System.currentTimeMillis() - sharedPreferences.getLong("time", -1)) <  TimeUnit.HOURS.toMillis(1)) {
                if (sharedPreferences.contains("contactsMap")) {
                    Type type = new TypeToken<Map<String, Contact>>(){}.getType();
                    Map<String, Contact> contactMap = gson.fromJson(sharedPreferences.getString("contactsMap", ""), type);
                    return getParseQuery(contactMap);
                }
            }
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
            phoneNumber = Utils.formatPhone(phoneNumber);
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

        return getParseQuery(contacts);
    }

    private List<Contact> getParseQuery(final Map<String, Contact> contactMap) {
        if (contactMap != null && !contactMap.isEmpty()) {
            List<ParseQuery<Person>> queries = new ArrayList<ParseQuery<Person>>();
            for (Map.Entry<String, Contact> entry : contactMap.entrySet()) {
                ParseQuery<Person> query = ParseQuery.getQuery(Person.class);
                query.whereEqualTo("phone", entry.getKey());
                queries.add(query);
            }
            ParseQuery<Person> mainQuery = ParseQuery.or(queries);
            mainQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
            ArrayList<Contact> contacts = new ArrayList<Contact>();
            try {
                List<Person> people = mainQuery.find();
                for (Person person : people) {
                    Contact contact = contactMap.get(person.getPhone());
                    contact.setUser(person);
                    contacts.add(contact);
                }
                return contacts;
            } catch (ParseException e) {
                Log.i("TAG", String.valueOf(e.getCode()));
                e.printStackTrace();
            }
        }
        return null;
    }
}
