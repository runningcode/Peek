package com.osacky.peek;

import android.content.Context;
import android.telephony.TelephonyManager;

public class Utils {

    public static String getUserPhoneNumber(Context context) {
        TelephonyManager telephonyManager =(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getLine1Number();
    }
}
