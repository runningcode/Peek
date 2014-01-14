package com.osacky.peek;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.osacky.peek.Models.Photo;
import com.parse.ParseAnalytics;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

public class CreatePeekActivity extends FragmentActivity {

    private Photo photo;
    private String phone;
    private CameraFragment topCameraFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        photo = new Photo();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_activity);
        ParseAnalytics.trackAppOpened(getIntent());

        Intent dismissIntent = new Intent(getApplicationContext(), NotificationCountdownService.class);
        dismissIntent.setAction(NotificationCountdownService.KILL_NOTIF);
        startService(dismissIntent);

        phone = Utils.getUserPhoneNumber(getApplicationContext());
        topCameraFragment = new CameraFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.top, topCameraFragment)
                .commit();

        photo.setSender(phone);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String receiver = extras.getString("username", "");
            if (receiver != null && !"".equals(receiver)) {
                photo.setReceiver(receiver);
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (photo.getTop() != null && photo.getBottom() != null) {
            photo.setTime(System.currentTimeMillis());
            photo.saveInBackground();

            ParseQuery<ParseInstallation> parseInstallationQuery = ParseInstallation.getQuery();
            parseInstallationQuery.whereEqualTo("username", photo.getReceiver());

            JSONObject data = new JSONObject();
            try {
                data.put("action", "com.osacky.peek.received");
                data.put("username", phone);
                data.put("time", System.currentTimeMillis());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ParsePush push = new ParsePush();
            push.setQuery(parseInstallationQuery);
            push.setData(data);
            push.sendInBackground();
        }
    }

    public Photo getCurrentPhoto() {
        return photo;
    }

    public void swapCamera(ParseFile photoParseFile, Bitmap preview) {
        getCurrentPhoto().setTop(photoParseFile);
        getSupportFragmentManager().beginTransaction().remove(topCameraFragment).commit();
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.top);
        frameLayout.removeAllViews();
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(preview);
        frameLayout.addView(imageView);
        getSupportFragmentManager().beginTransaction().add(R.id.bottom, new FrontCameraFragment()).commit();
    }
}
