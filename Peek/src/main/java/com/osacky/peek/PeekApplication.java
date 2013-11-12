package com.osacky.peek;

import android.app.Application;
import android.os.Build;

import com.osacky.peek.Models.Person;
import com.osacky.peek.Models.Photo;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;

public class PeekApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        String appID = getResources().getString(R.string.app_id);
        String clientKey = getResources().getString(R.string.client_key);

        ParseObject.registerSubclass(Person.class);
        ParseObject.registerSubclass(Photo.class);
        Parse.initialize(this, appID, clientKey);

        PushService.setDefaultPushCallback(this, PeekActivity.class);
        ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
        parseInstallation.put("modelNumber", Build.MODEL);
        parseInstallation.put("serial", Build.SERIAL);
        parseInstallation.put("deviceName", Build.MANUFACTURER + " " + Build.PRODUCT);
        parseInstallation.saveEventually();
    }
}
