package com.osacky.peek;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Patterns;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.osacky.peek.Models.Contact;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.regex.Pattern;

public class Utils {

    public static String getUserPhoneNumber(Context context) {
        TelephonyManager telephonyManager =(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String phone = telephonyManager.getLine1Number();
        if (phone == null) {
            return null;
        }
        return formatPhone(telephonyManager.getLine1Number());
    }

    public static String getUserEmail(Context context) {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        AccountManager accountManager = AccountManager.get(context);
        if (accountManager != null) {
            for (Account account : accountManager.getAccounts()) {
                if (emailPattern.matcher(account.name).matches()) {
                    return account.name;
                }
            }
        }
        return null;
    }

    public static String formatPhone(String phone) {
        if (phone == null) {
            return null;
        }
        phone = phone.replaceAll("\\D", "");
        if (phone.startsWith("1")) {
            return phone.substring(1);
        } else {
            return phone;
        }
    }

    public static void getNameAndPic(Context context, String username, NotificationCompat.Builder builder) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.contains("contactsMap")) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Contact>>(){}.getType();
            Map<String, Contact> contacts = gson.fromJson(sharedPreferences.getString("contactsMap", ""), type);
            if (contacts.containsKey(username)) {
                Contact contact = contacts.get(username);
                builder.setContentText("Peek request from " + contact.getName());
                builder.setTicker("Peek request from " + contact.getName());
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
                    builder.setLargeIcon(userImage);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
