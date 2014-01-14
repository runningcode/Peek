package com.osacky.peek;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.osacky.peek.Models.Contact;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class PeekReceivedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        try {
            JSONObject jsonObject = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            String username = jsonObject.getString("username");
            Long time = jsonObject.getLong("time");

            Intent peekIntent = new Intent(context, PeekActivity.class);
            peekIntent.putExtra("username", username);
            peekIntent.putExtra("time", time);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, peekIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.notification)
                    .setContentTitle("You got a peek!")
                    .setContentText(username)
                    .setTicker("You got a peek from " + username)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (sharedPreferences.contains("contactsMap")) {
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, Contact>>(){}.getType();
                Map<String, Contact> contacts = gson.fromJson(sharedPreferences.getString("contactsMap", ""), type);
                if (contacts.containsKey(username)) {
                    Contact contact = contacts.get(username);
                    builder.setContentText(contact.getName());
                    builder.setTicker("You got a peek from " + contact.getName());
                    try {
                        Bitmap userImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(contact.getPhotoURI()));
                        int width = (int) context.getResources().getDimension(android.R.dimen.notification_large_icon_width);
                        int height = (int) context.getResources().getDimension(android.R.dimen.notification_large_icon_height);
                        // this is to fix a samsung bug
                        if (width > height) {
                            userImage = Bitmap.createScaledBitmap(userImage, width, width, false);
                        } else {
                            userImage = Bitmap.createScaledBitmap(userImage, height, height, false);
                        }
                        userImage = Bitmap.createScaledBitmap(userImage, width, height, false);
                        builder.setLargeIcon(userImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, builder.build());

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
