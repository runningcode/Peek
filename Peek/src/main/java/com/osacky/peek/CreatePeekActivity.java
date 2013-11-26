package com.osacky.peek;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.osacky.peek.Models.Photo;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

public class CreatePeekActivity extends FragmentActivity {

    private Photo photo;
    private Bitmap top;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        photo = new Photo();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_activity);
        ParseAnalytics.trackAppOpened(getIntent());

        phone = Utils.getUserPhoneNumber(getApplicationContext());

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );


        getSupportFragmentManager().beginTransaction()
                .add(R.id.top, new CameraFragment())
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

    public void setTop(Bitmap bitmap) {
        top = bitmap;
    }

    public void swapCamera() {
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.top);
        frameLayout.removeAllViews();
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(top);
        frameLayout.addView(imageView);
        getSupportFragmentManager().beginTransaction().add(R.id.bottom, new FrontCameraFragment()).commit();
    }
}
