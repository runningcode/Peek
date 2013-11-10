package com.osacky.peek;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.osacky.peek.Models.Contact;
import com.osacky.peek.Models.Photo;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQueryAdapter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ParsePhotoAdapter extends ParseQueryAdapter<Photo> {

    private LayoutInflater layoutInflater;
    private SharedPreferences sharedPreferences;

    private Map<String, Bitmap> topMap;
    private Map<String, Bitmap> bottomMap;

    public ParsePhotoAdapter(Context context, QueryFactory queryFactory) {
        super(context, queryFactory);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        topMap = new HashMap<String, Bitmap>();
        bottomMap = new HashMap<String, Bitmap>();
    }

    @Override
    public View getItemView(Photo object, View v, ViewGroup parent) {
        if (v == null) {
            v = layoutInflater.inflate(R.layout.feed_item, parent, false);
        }
        final TextView textView = (TextView) v.findViewById(R.id.text1);
        final ImageView senderImage = (ImageView) v.findViewById(R.id.userprofile);
        final ImageView imageView = (ImageView) v.findViewById(R.id.icon);
        final ImageView bottom = (ImageView)v.findViewById(R.id.second);

        String sender = object.getSender();
        textView.setText(sender);

        if (sharedPreferences.contains("contactsMap")) {
            Map<String, Contact> contactMap;
            Type type = new TypeToken<Map<String, Contact>>(){}.getType();
            contactMap = new Gson().fromJson(sharedPreferences.getString("contactsMap", ""), type);
            if (contactMap.containsKey(sender)) {
                Contact contact = contactMap.get(sender);
                if (contact.getPhotoURI() != null) {
                    senderImage.setImageURI(Uri.parse(contact.getPhotoURI()));
                }
                textView.setText(contact.getName());
            }
        }

        if (topMap.containsKey(object.getTime()) && bottomMap.containsKey(object.getTime())) {
            imageView.setImageBitmap(topMap.get(object.getTime()));
            bottom.setImageBitmap(bottomMap.get(object.getTime()));
        } else {
            ParseFile top = object.getTop();
            if (top != null) {
                top.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, ParseException e) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
            ParseFile bot = object.getBottom();
            if (bot != null) {
                bot.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, ParseException e) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        bottom.setImageBitmap(bitmap);
                    }
                });
            }
        }
        return v;
    }
}
