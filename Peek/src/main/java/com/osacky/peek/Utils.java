package com.osacky.peek;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Patterns;

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
}
