package com.osacky.peek;

import android.app.Application;
import android.os.Build;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class PeekApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, "NXIHRKwZCV2JAdSLCCPajcnbBLVBrUj2ZPJ1JBO1",
                "EgULt8COdAe47MCMZ1HtvjnFtDttMBCZr5a9ITbF");

        PushService.setDefaultPushCallback(this, PeekActivity.class);
        ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
        parseInstallation.put("modelNumber", Build.MODEL);
        parseInstallation.put("serial", Build.SERIAL);
        parseInstallation.put("deviceName", Build.MANUFACTURER + " " + Build.PRODUCT);
        parseInstallation.saveInBackground();
    }
}
